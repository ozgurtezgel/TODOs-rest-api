package com.example.TODOsRestApi.api;

import com.example.TODOsRestApi.model.TODO;
import com.example.TODOsRestApi.model.User;
import com.example.TODOsRestApi.service.UserService;
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
    public User registerUser(@Valid @RequestBody User user, @RequestHeader(value = "Authorization", required = false) String accessToken) {
        LOGGER.info("POST " + BASE_URL + "/register");
        System.out.println(user);
        return userService.registerUser(user, accessToken);
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String accessToken) {
        LOGGER.info("DELETE " + BASE_URL + "/{}", id);
        userService.deleteUserById(id, accessToken);
    }

    @PostMapping(path = "{userId}/todos")
    @ResponseStatus(HttpStatus.CREATED)
    public TODO createTODO(@Valid @RequestBody TODO todo, @PathVariable("userId") Long userId, @RequestHeader(value = "Authorization", required = false) String accessToken) {
        LOGGER.info("POST " + BASE_URL + "/{}/todos", userId);
        todo.setUserId(userId);
        System.out.println(todo);
        return userService.createTODO(todo, accessToken);
    }

    @GetMapping(path = "{id}/todos")
    @ResponseStatus(HttpStatus.OK)
    public List<TODO> getTODOs(@PathVariable("id") Long id, @RequestParam(required = false, value = "title", defaultValue = "") String title, @RequestParam(required = false, value = "status", defaultValue = "") String status) {
        LOGGER.info("GET " + BASE_URL + "/{}/todos", id);
        return Arrays.stream(userService.getTODOs(id, title, status)).toList();
    }

//    @GetMapping(path = "{id}/todos")
//    @ResponseStatus(HttpStatus.OK)
//    public List<TODO> getTODOs(@PathVariable("id") Long id) {
//        LOGGER.info("GET " + BASE_URL + "/{}/todos", id);
//        return Arrays.stream(userService.getTODOs(id)).toList();
//    }

    @GetMapping(path = "{userId}/{id}/todo")
    @ResponseStatus(HttpStatus.OK)
    public TODO getTODO(@PathVariable("userId") Long userId, @PathVariable("id") Long id) {
        LOGGER.info("GET " + BASE_URL + "/{}/{}/todo", userId, id);
        return userService.getTODO(userId, id);
    }
}
