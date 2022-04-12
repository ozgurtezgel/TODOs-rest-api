package com.example.TodosRestApi;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
        headers.set("Authorization", accessToken);
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
        headers.set("Authorization", accessToken);
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
        TODOItem todo = new TODOItem(-1L, -1L, "2022", "2023-01-01", "active");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        HttpEntity<TODOItem> entity = new HttpEntity<>(todo, headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + todo.getUserId() + "/todos", HttpMethod.POST, entity, TODOItem.class)).thenReturn(new ResponseEntity<>(todo, HttpStatus.CREATED));

        // act
        TODOItem response = userRestClient.createTODO(todo, todo.getUserId());

        // assert
        assertEquals(todo, response);
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/" + todo.getUserId() + "/todos", HttpMethod.POST, entity, TODOItem.class);
    }

    @Test
    public void shouldGetTODOsSuccessfully() {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem[] todos = {firstTODO, secondTODO, thirdTODO};
        List<TODOItem> expected = List.of(firstTODO, secondTODO, thirdTODO);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act
        List<TODOItem> response = userRestClient.getTODOs(userId);

        // assert
        assertEquals(expected.size(), response.size());
        assertEquals(expected, response);
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class);
    }
}