package com.example.TODOsRestApi.service;
import com.example.TODOsRestApi.UserRestClient;
import com.example.TODOsRestApi.model.TODO;
import com.example.TODOsRestApi.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRestClient userRestClientMock;

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
        TODO todo = new TODO(-1L, -1L, "2022", "2023-01-01", "active");
        when(userRestClientMock.createTODO(todo, -1L)).thenReturn(todo);

        // act
        TODO response = userService.createTODO(todo);

        // assert
        assertEquals(todo, response);
        verify(userRestClientMock, times(1)).createTODO(todo, todo.getUserId());
    }

    @Test
    public void shouldGetTODOsSuccessfully() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2022-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO[] response = userService.getTODOs(userId, "", "");

        // assert
        assertArrayEquals(todos, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetTODOSuccessfully() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2022-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO response = userService.getTODO(userId, -2L);

        // assert
        assertEquals(secondTODO, response);
        assertEquals(secondTODO.getTitle(), response.getTitle());
         verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTwoTODOsWithBothParametersSuccessfully() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2023-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO fourthTODO = new TODO(-4L, userId, "Outdoor sport", "2023-01-06", "pending");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO, fourthTODO};
        TODO[] expected = {secondTODO, fourthTODO};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO[] response = userService.getTODOs(userId, "Spor", "pending");

        // assert
        assertArrayEquals(response, expected);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldReturnEmptyList() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2023-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO fourthTODO = new TODO(-4L, userId, "Outdoor sport", "2023-01-06", "pending");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO, fourthTODO};
        TODO[] expected = {};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO[] response = userService.getTODOs(userId, "Spor", "completed");

        // assert
        assertArrayEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTODOWithTitle() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2022-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO};
        TODO[] expected = {secondTODO};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO[] response = userService.getTODOs(userId, "Sport", "");

        // assert
        assertArrayEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTODOsWithTitle() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2022-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO fourthTODO = new TODO(-4L, userId, "Outdoor sport", "2021-01-06", "completed");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO, fourthTODO};
        TODO[] expected = {secondTODO, fourthTODO};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO[] response = userService.getTODOs(userId, "Sport", "");

        // assert
        assertArrayEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }

    @Test
    public void shouldGetSpecifiedTODOsWithStatus() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2022-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO};
        TODO[] expected = {firstTODO, secondTODO};
        when(userRestClientMock.getTODOs(userId)).thenReturn(todos);

        // act
        TODO[] response = userService.getTODOs(userId, "", "pending");

        // assert
        assertArrayEquals(expected, response);
        verify(userRestClientMock, times(1)).getTODOs(userId);
    }
}