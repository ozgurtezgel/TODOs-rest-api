package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import com.example.TodosRestApi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(path = UserController.BASE_URL)
public class UserController {

    static final String BASE_URL = "/users";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping(path = "register")
    @ResponseStatus(HttpStatus.CREATED)
    public User registerUser(@Valid @RequestBody User user) {
        System.out.println(user);
        return userService.registerUser(user);
    }

    //  @RequestHeader(value = "Authorization", required = false) String accessToken
    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
    }

    @PostMapping(path = "{userId}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TODOItem createTODO(@Valid @RequestBody TODOItem todo, @PathVariable("userId") Long userId) {
        todo.setUserId(userId);
        return userService.createTODO(todo);
    }

    @GetMapping(path = "{id}/todos")
    @ResponseStatus(HttpStatus.OK)
    public List<TODOItem> getTODOs(@PathVariable("id") Long id, @RequestParam(required = false, value = "title", defaultValue = "") String title, @RequestParam(required = false, value = "status", defaultValue = "") String status) {
        return userService.getTODOs(id, title, status);
    }

//    @GetMapping(path = "{id}/todos")
//    @ResponseStatus(HttpStatus.OK)
//    public List<TODO> getTODOs(@PathVariable("id") Long id) {
//        LOGGER.info("GET " + BASE_URL + "/{}/todos", id);
//        return Arrays.stream(userService.getTODOs(id)).toList();
//    }

    @GetMapping(path = "{userId}/{id}/todo")
    @ResponseStatus(HttpStatus.OK)
    public TODOItem getTODO(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        return userService.getTODO(userId, id);
    }
}
