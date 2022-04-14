package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import com.example.TodosRestApi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

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
    public List<TODOItem> getTODOs(@PathVariable("id") Long id, @RequestParam(required = false, value = "title", defaultValue = "") String title, @RequestParam(required = false, value = "status", defaultValue = "") String status,
                                   @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {

        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            return userService.getTODOs(id, title, status);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
    }

    @GetMapping(path = "{userId}/{id}/todo")
    @ResponseStatus(HttpStatus.OK)
    public TODOItem getTODO(@PathVariable("userId") Long userId, @PathVariable("id") Long id, @RequestHeader(value = "clientId") String clientId, @RequestHeader(value = "clientSecret") String clientSecret) {
        if (clientId.equals(ID) && clientSecret.equals(secret)) {
            return userService.getTODO(userId, id);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid client id or secret");
    }
}
