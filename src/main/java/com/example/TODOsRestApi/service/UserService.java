package com.example.TODOsRestApi.service;

import com.example.TODOsRestApi.UserRestClient;
import com.example.TODOsRestApi.model.TODO;
import com.example.TODOsRestApi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRestClient userRestClient;

    public UserService(UserRestClient userRestClient) {
        this.userRestClient = userRestClient;
    }

    public User registerUser(User user) {
        LOGGER.info("Register one user {}", user);
        return userRestClient.registerUser(user);
    }

    public void deleteUserById(Long id) {
        LOGGER.info("Delete the user with the id: {}", id);
        userRestClient.deleteUserByID(id);
    }

    public TODO createTODO(TODO todo) {
        LOGGER.info("Create a TODO {} for the user with id: {}", todo, todo.getUserId());
        return userRestClient.createTODO(todo, todo.getUserId());
    }

    public TODO[] getTODOs(Long userId, String title, String status) {
        LOGGER.info("Get the TODO of the user with the id: {} matching the parameters title: {}, status: {}", userId, title, status);
        TODO[] todos = userRestClient.getTODOs(userId);
        List<TODO> temp = new ArrayList<>();
        if (status.equals("")) {
            for (int i = 0; i < todos.length ; i++) {
                if (todos[i].getTitle().toLowerCase().contains(title.toLowerCase())) {
                    temp.add(todos[i]);
                }
            }
        } else {
            for (int i = 0; i < todos.length ; i++) {
                if (todos[i].getStatus().equals(status) && todos[i].getTitle().toLowerCase().contains(title.toLowerCase())) {
                    temp.add(todos[i]);
                }
            }
        }
        TODO[] result = new TODO[temp.size()];
        for (int i = 0; i < temp.size(); i++) {
            result[i] = temp.get(i);
        }
        return result;
    }

//    public TODO[] getTODOs(Long id) {
//        LOGGER.info("Get TODOs of the user with the id: {}", id);
//        return userRestClient.getTODOs(id);
//    }

    public TODO getTODO(Long userId, Long id) {
        LOGGER.info("Get the TODO with the id: {} of the User with the id: {}", id, userId);
        TODO[] todos = userRestClient.getTODOs(userId);
        for (int i = 0; i < todos.length; i++) {
            if (todos[i].getId().equals(id)) {
                return todos[i];
            }
        }
        throw new NoSuchElementException("There is no TODO with the given id");
    }
}
