package com.example.TodosRestApi.service;

import com.example.TodosRestApi.UserRestClient;
import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

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

    @Cacheable(cacheNames = {"todoCache"}, key = "#userId.toString() + #title.toString() + #status.toString()")
    public List<TODOItem> getTODOs(Long userId, Optional<String> title, Optional<String> status) {
        LOGGER.info("Get the TODO of the user with the id: {} matching the parameters title: {}, status: {}", userId, title, status);
        List<TODOItem> todos = userRestClient.getTODOs(userId);
        List<TODOItem> result = new ArrayList<>();

        Stream<TODOItem> stream = todos.stream();
        if (status.isPresent()) {
            stream = stream.filter(todoItem -> todoItem.getStatus().equals(status.get()));
        }
        if (title.isPresent()) {
            stream = stream.filter(todoItem -> todoItem.getTitle().toLowerCase().contains(title.get().toLowerCase()));
        }
        result = stream.toList();
        return result;
    }

    @Cacheable(cacheNames = {"todoCache"}, key = "#userId.toString() + #id.toString()")
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
