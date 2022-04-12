package com.example.TodosRestApi.service;
import com.example.TodosRestApi.UserRestClient;
import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRestClient userRestClientMock;
    private static final String accessToken = "4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8";

    @Test
    public void shouldRegisterUserSuccessfully() {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "active");
        when(userRestClientMock.registerUser(user)).thenReturn(user);

        // act
        User response = userService.registerUser(user);

        // assert
        verify(userRestClientMock, times(1)).registerUser(user);
        assertEquals(user, response);
    }

    @Test
    public void shouldDeleteUserSuccessfully() {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "active");

        // act
        userService.deleteUserById(user.getId());

        // assert
        verify(userRestClientMock, times(1)).deleteUserByID(user.getId());
    }

    @Test
    public void shouldCreateTODOSuccessfully() {
        // arrange
        TODOItem todo = new TODOItem(-1L, -1L, "2022", "2023-01-01", "active");
        when(userRestClientMock.createTODO(todo, -1L)).thenReturn(todo);

        // act
        TODOItem response = userService.createTODO(todo);

        // assert
        assertEquals(todo, response);
        verify(userRestClientMock, times(1)).createTODO(todo, todo.getUserId());
    }

    @Test
    public void shouldGetTODOsSuccessfully() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO);
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        List<TODOItem> response = userService.getTODOs(userId, "", "");

        // assert
        assertEquals(todos, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetTODOSuccessfully() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO);
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODOItem response = userService.getTODO(userId, -2L);

        // assert
        assertEquals(secondTODO, response);
        assertEquals(secondTODO.getTitle(), response.getTitle());
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTwoTODOsWithBothParametersSuccessfully() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2023-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem fourthTODO = new TODOItem(-4L, userId, "Outdoor sport", "2023-01-06", "pending");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO, fourthTODO);
        List<TODOItem> expected = List.of(secondTODO, fourthTODO);
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        List<TODOItem> response = userService.getTODOs(userId, "Spor", "pending");

        // assert
        assertEquals(response, expected);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldReturnEmptyList() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2023-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem fourthTODO = new TODOItem(-4L, userId, "Outdoor sport", "2023-01-06", "pending");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO, fourthTODO);
        List<TODOItem> expected = new ArrayList<>();
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        List<TODOItem> response = userService.getTODOs(userId, "Spor", "completed");

        // assert
        assertEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTODOWithTitle() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO);
        List<TODOItem> expected = List.of(secondTODO);
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        List<TODOItem> response = userService.getTODOs(userId, "Sport", "");

        // assert
        assertEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTODOsWithTitle() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem fourthTODO = new TODOItem(-4L, userId, "Outdoor sport", "2021-01-06", "completed");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO, fourthTODO);
        List<TODOItem> expected = List.of(secondTODO, fourthTODO);
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        List<TODOItem> response = userService.getTODOs(userId, "Sport", "");

        // assert
        assertEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTODOsWithStatus() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO);
        List<TODOItem> expected = List.of(firstTODO, secondTODO);
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        List<TODOItem> response = userService.getTODOs(userId, "", "pending");

        // assert
        assertEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }
}