package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import com.example.TodosRestApi.service.UserService;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping(path = UserController.BASE_URL)
public class UserController {

    static final String BASE_URL = "/users";
    private final UserService userService;
    @Value("${client.id}")
    private String ID;
    @Value("${client.secret}")
    private String secret;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@Valid @RequestBody User user, @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {
        System.out.println(user);
        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            return userService.registerUser(user);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("id") Long id, @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {
        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            userService.deleteUserById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
        }
    }

    @PostMapping(path = "{userId}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TODOItem createTODO(@Valid @RequestBody TODOItem todo, @PathVariable("userId") Long userId, @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {
        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            todo.setUserId(userId);
            return userService.createTODO(todo);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
    }

    @GetMapping(path = "{id}/todos")
    @ResponseStatus(HttpStatus.OK)
    @Retry(name = "todoSearch")
    public CollectionModel<TODOItem> getTODOs(@PathVariable("id") Long id, @RequestParam(required = false, value = "title") Optional<String> title, @RequestParam(required = false, value = "status") Optional<String> status,
                                              @RequestParam(required = false, value = "pageSize", defaultValue = "20") Integer pageSize, @RequestParam(required = false, value = "page", defaultValue = "0") Integer page,
                                              @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {

        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            List<TODOItem> todos = userService.getTODOs(id, title, status);
            Pageable paging = PageRequest.of(page, pageSize);
            int start = paging.getPageNumber() * paging.getPageSize();
            if (start > todos.size()) {
                return CollectionModel.of(new ArrayList<>());
            }
            int end = Math.min(start + paging.getPageSize(), todos.size());
            CollectionModel<TODOItem> result;
            if (end >= todos.size()) {
                Page<TODOItem> pageItem = new PageImpl<>(todos.subList(start, end), paging, todos.size());
                result = CollectionModel.of(pageItem);
            } else {
                Link nextLink = linkTo(methodOn(UserController.class).getTODOs(id, title, status, pageSize, page+1, clientId, clientSecret)).withRel("next");
                nextLink = nextLink.expand();
                Page<TODOItem> pageItem = new PageImpl<>(todos.subList(start, end), paging, todos.size());
                result = CollectionModel.of(pageItem, nextLink);
            }
            return result;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
    }

    @GetMapping(path = "{userId}/{id}/todo")
    @ResponseStatus(HttpStatus.OK)
    @Retry(name = "todoSearch")
    public TODOItem getTODO(@PathVariable("userId") Long userId, @PathVariable("id") Long id, @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {
        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            return userService.getTODO(userId, id);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
    }
}
