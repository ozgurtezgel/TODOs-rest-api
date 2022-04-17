package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import com.example.TodosRestApi.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Value("${client.id}")
    private String ID;
    @Value("${client.secret}")
    private String secret;

    public UserControllerTest() {
    }

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "active");
        when(userServiceMock.registerUser(user)).thenReturn(user);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("clientId", ID)
                .header("clientSecret", secret)
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
                .header("clientId", ID)
                .header("clientSecret", secret)
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
                .header("clientId", ID)
                .header("clientSecret", secret)
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
                .header("clientId", ID)
                .header("clientSecret", secret)
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
                .header("clientId", ID)
                .header("clientSecret", secret)
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
                mockMvc.perform(get("/users/-1/-3/todo").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret))
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
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem fifthTODO = new TODOItem(-3L, userId, "Hobbies", "2023-02-20", "completed");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO, fourthTODO, fifthTODO, sixthTODO);
        when(userServiceMock.getTODOs(userId, "", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(6)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(firstTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].due_on", is(secondTODO.getDue_on())))
                        .andExpect(jsonPath("_embedded.tODOItemList[2].status", is(thirdTODO.getStatus())))
                        .andExpect(jsonPath("_embedded.tODOItemList[3].title", is(fourthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[4].due_on", is(fifthTODO.getDue_on())))
                        .andExpect(jsonPath("_embedded.tODOItemList[5].status", is(sixthTODO.getStatus())));
    }

    @Test
    public void shouldGetSpecifiedTwoTODOsWithBothParameters() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        List<TODOItem> todos = List.of(fourthTODO, sixthTODO);
        when(userServiceMock.getTODOs(userId, "job", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("title", "job"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(2)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(fourthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].due_on", is(fourthTODO.getDue_on())))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].status", is(fourthTODO.getStatus())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(sixthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].due_on", is(sixthTODO.getDue_on())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].status", is(sixthTODO.getStatus())));
        verify(userServiceMock, times(1)).getTODOs(userId, "job", "");
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
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("title", "sport"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(1)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is("Sport")))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].due_on", is("2022-06-30")))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].status", is("pending")));
        verify(userServiceMock, times(1)).getTODOs(userId, "sport", "");
    }

    @Test
    public void shouldGetSpecifiedTODOsWithPageSize() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem fifthTODO = new TODOItem(-3L, userId, "Hobbies", "2023-02-20", "completed");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO, fourthTODO, fifthTODO, sixthTODO);
        when(userServiceMock.getTODOs(userId, "", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("pageSize", "3"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(3)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(firstTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(secondTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[2].title", is(thirdTODO.getTitle())))
                        .andExpect(jsonPath("_links.next.href", is("http://localhost/users/-1/todos?title=&status=&pageSize=3&page=1")));
        verify(userServiceMock, times(1)).getTODOs(userId, "", "");
    }

    @Test
    public void shouldGetSpecifiedTODOsWithPageSizeAndStatus() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem fifthTODO = new TODOItem(-3L, userId, "Hobbies", "2023-02-20", "completed");
        List<TODOItem> todos = List.of(thirdTODO, fourthTODO, fifthTODO);
        when(userServiceMock.getTODOs(userId, "", "completed")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("status", "completed")
                                .param("pageSize", "2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(2)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(thirdTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(fourthTODO.getTitle())))
                        .andExpect(jsonPath("_links.next.href", is("http://localhost/users/-1/todos?title=&status=completed&pageSize=2&page=1")));
        verify(userServiceMock, times(1)).getTODOs(userId, "", "completed");
    }

    @Test
    public void shouldGetSpecifiedTODOsWithPageSizeAndPage() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem fifthTODO = new TODOItem(-3L, userId, "Hobbies", "2023-02-20", "completed");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        List<TODOItem> todos = List.of(firstTODO, secondTODO, thirdTODO, fourthTODO, fifthTODO, sixthTODO);
        when(userServiceMock.getTODOs(userId, "", "")).thenReturn(todos);

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("pageSize", "2")
                                .param("page", "1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(2)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(thirdTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(fourthTODO.getTitle())))
                        .andExpect(jsonPath("_links.next.href", is("http://localhost/users/-1/todos?title=&status=&pageSize=2&page=2")));
        verify(userServiceMock, times(1)).getTODOs(userId, "", "");
    }
}