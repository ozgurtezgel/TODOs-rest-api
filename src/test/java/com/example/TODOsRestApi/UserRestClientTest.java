package com.example.TODOsRestApi;

import com.example.TODOsRestApi.model.TODO;
import com.example.TODOsRestApi.model.User;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRestClientTest {

    private static final String REQUEST_URI = "https://gorest.co.in/public/v2/users";
    private static final String accessToken = "4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8";
    @InjectMocks
    private UserRestClient userRestClient;
    @Mock
    private RestTemplate restTemplateMock;

    @Test
    public void shouldRegisterUserSuccessfully() {
        // arrange
        User user = new User(-1L, "John", "john@gmail.com", "male", "active");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ accessToken);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        when(restTemplateMock.exchange(REQUEST_URI, HttpMethod.POST, entity, User.class)).thenReturn(new ResponseEntity<>(user, HttpStatus.CREATED));

        // act
        User response = userRestClient.registerUser(user);

        // assert
        assertEquals(user, response);
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI, HttpMethod.POST, entity, User.class);
    }

    @Test
    public void shouldDeleteUserSuccessfully() {
        // arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/-1", HttpMethod.DELETE, entity, Void.class)).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        // act
        userRestClient.deleteUserByID(-1L);

        // assert
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/-1", HttpMethod.DELETE, entity, Void.class);
    }

    @Test
    public void shouldCreateTODOSuccessfully() {
        // arrange
        TODO todo = new TODO(-1L, -1L, "2022", "2023-01-01", "active");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ accessToken);
        HttpEntity<TODO> entity = new HttpEntity<>(todo, headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + todo.getUserId() + "/todos", HttpMethod.POST, entity, TODO.class)).thenReturn(new ResponseEntity<>(todo, HttpStatus.CREATED));

        // act
        TODO response = userRestClient.createTODO(todo, todo.getUserId());

        // assert
        assertEquals(todo, response);
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/" + todo.getUserId() + "/todos", HttpMethod.POST, entity, TODO.class);
    }

    @Test
    public void shouldGetTODOsSuccessfully() {
        // arrange
        Long userId = -1L;
        TODO firstTODO = new TODO(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODO secondTODO = new TODO(-2L, userId, "Sport", "2022-06-30", "pending");
        TODO thirdTODO = new TODO(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODO[] todos = {firstTODO, secondTODO, thirdTODO};
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ accessToken);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODO[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act
        TODO[] response = userRestClient.getTODOs(userId);

        // assert
        assertEquals(todos.length, response.length);
        assertEquals(todos, response);
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODO[].class);
    }
}