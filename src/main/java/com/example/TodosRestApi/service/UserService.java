package com.example.TodosRestApi.service;

import com.example.TodosRestApi.UserRestClient;
import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
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

    public TODOItem createTODO(TODOItem todo) {
        LOGGER.info("Create a TODO {} for the user with id: {}", todo, todo.getUserId());
        return userRestClient.createTODO(todo, todo.getUserId());
    }

    public List<TODOItem> getTODOs(Long userId, String title, String status) {
        LOGGER.info("Get the TODO of the user with the id: {} matching the parameters title: {}, status: {}", userId, title, status);
        List<TODOItem> todos = userRestClient.getTODOs(userId);
        List<TODOItem> result = new ArrayList<>();

        if (status.equals("")) {
            result = todos.stream().filter(todoItem -> todoItem.getTitle().toLowerCase().contains(title.toLowerCase())).toList();
        } else {
            result = todos.stream().filter(todoItem -> todoItem.getTitle().toLowerCase().contains(title.toLowerCase()) && todoItem.getStatus().equals(status)).toList();
        }

//        if (status.equals("")) {
//            for (int i = 0; i < todos.size() ; i++) {
//                if (todos.get(i).getTitle().toLowerCase().contains(title.toLowerCase())) {
//                    result.add(todos.get(i));
//                }
//            }
//        } else {
//            for (int i = 0; i < todos.size() ; i++) {
//                if (todos.get(i).getStatus().equals(status) && todos.get(i).getTitle().toLowerCase().contains(title.toLowerCase())) {
//                    result.add(todos.get(i));
//                }
//            }
//        }
        return result;
    }

    public TODOItem getTODO(Long userId, Long id) {
        LOGGER.info("Get the TODO with the id: {} of the User with the id: {}", id, userId);
        List<TODOItem> todos = userRestClient.getTODOs(userId);
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(id)) {
                return todos.get(i);
            }
        }
        throw new NoSuchElementException("There is no TODO with the given id");
    }
}
