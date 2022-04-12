package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import com.example.TodosRestApi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userServiceMock;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    private static final String accessToken = "4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8";

    public UserControllerTest() {
    }

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "active");
        when(userServiceMock.registerUser(user)).thenReturn(user);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        // act & assert
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.gender", is("male")));
    }

    @Test
    public void shouldReturnStatusCode400WhenRegisteringUser() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "mal", "active");
        when(userServiceMock.registerUser(user)).thenReturn(user);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        // act & assert
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteUserSuccessfully() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "active");

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/users/-1")
                .header("Authorization", accessToken)
                .contentType(MediaType.APPLICATION_JSON);

        // act & assert
        mockMvc.perform(mockRequest).andExpect(status().isNoContent());
        verify(userServiceMock, times(1)).deleteUserById(user.getId());
    }

    @Test
    public void shouldCreateTODOSuccessfully() throws Exception {
        // arrange
        TODOItem todo = new TODOItem(-1L, -1L, "2022", "2023-01-01", "pending");
        when(userServiceMock.createTODO(todo)).thenReturn(todo);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users/-1/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(todo));

        // act & assert
        mockMvc.perform(mockRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.title", is("2022")))
                .andExpect(jsonPath("$.due_on", is("2023-01-01")));
    }

    @Test
    public void shouldReturnStatusCode400WhenCreatingTODO() throws Exception {
        // arrange
        TODOItem todo = new TODOItem(-1L, -1L, "2022", "2023-01-01", "active");
        when(userServiceMock.createTODO(todo)).thenReturn(todo);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users/-1/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", accessToken)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(todo));

        // act & assert
        mockMvc.perform(mockRequest).andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGetTODOSuccessfully() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        when(userServiceMock.getTODO(userId, -3L)).thenReturn(thirdTODO);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/-3/todo").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.title", is("Groceries")))
                        .andExpect(jsonPath("$.due_on", is("2022-03-16")))
                        .andExpect(jsonPath("$.status", is("completed")));
    }

    @Test
    public void shouldGetTODOsSuccessfully() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO);
        when(userServiceMock.getTODOs(userId, "", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(3)))
                        .andExpect(jsonPath("$[0].title", is("Math Class")))
                        .andExpect(jsonPath("$[1].due_on", is("2022-06-30")))
                        .andExpect(jsonPath("$[2].status", is("completed")));
    }

    @Test
    public void shouldGetSpecifiedTwoTODOsWithBothParameters() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2023-06-30", "pending");
        TODOItem fourthTODO = new TODOItem(-4L, userId, "Outdoor sport", "2023-01-06", "pending");
        List<TODOItem> todos = List.of(secondTODO, fourthTODO);
        when(userServiceMock.getTODOs(userId, "spor", "pending")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos?title=spor&status=pending").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].due_on", is("2023-06-30")))
                        .andExpect(jsonPath("$[1].due_on", is("2023-01-06")))
                        .andExpect(jsonPath("$[0].title", is("Sport")))
                        .andExpect(jsonPath("$[1].title", is("Outdoor sport")))
                        .andExpect(jsonPath("$[0].status", is("pending")))
                        .andExpect(jsonPath("$[1].status", is("pending")));
        verify(userServiceMock, times(1)).getTODOs(userId, "spor", "pending");
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {
        // arrange
        Long userId = -1L;
        List<TODOItem> todos = new ArrayList<>();
        when(userServiceMock.getTODOs(userId, "Spor", "completed")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos?title=Spor&status=completed").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(0)));
        verify(userServiceMock, times(1)).getTODOs(userId, "Spor", "completed");
    }

    @Test
    public void shouldGetSpecifiedTODOWithTitle() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        List<TODOItem> todos = List.of(secondTODO);
        when(userServiceMock.getTODOs(userId, "sport", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos?title=sport").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(1)))
                        .andExpect(jsonPath("$[0].title", is("Sport")))
                        .andExpect(jsonPath("$[0].due_on", is("2022-06-30")))
                        .andExpect(jsonPath("$[0].status", is("pending")));
        verify(userServiceMock, times(1)).getTODOs(userId, "sport", "");
    }

    @Test
    public void shouldGetSpecifiedTODOsWithTitle() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem fourthTODO = new TODOItem(-4L, userId, "Outdoor sport", "2021-01-06", "completed");
        List<TODOItem> todos = List.of(secondTODO, fourthTODO);
        when(userServiceMock.getTODOs(userId, "Sport", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos?title=Sport").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].title", is("Sport")))
                        .andExpect(jsonPath("$[0].status", is("pending")))
                        .andExpect(jsonPath("$[1].title", is("Outdoor sport")))
                        .andExpect(jsonPath("$[1].status", is("completed")));
        verify(userServiceMock, times(1)).getTODOs(userId, "Sport", "");
    }

    @Test
    public void shouldGetSpecifiedTODOsWithStatus() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        List<TODOItem> todos = List.of(firstTODO, secondTODO);
        when(userServiceMock.getTODOs(userId, "", "pending")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos?status=pending").contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(2)))
                        .andExpect(jsonPath("$[0].title", is("Math Class")))
                        .andExpect(jsonPath("$[0].status", is("pending")))
                        .andExpect(jsonPath("$[1].title", is("Sport")))
                        .andExpect(jsonPath("$[1].status", is("pending")));
        verify(userServiceMock, times(1)).getTODOs(userId, "", "pending");
    }
}