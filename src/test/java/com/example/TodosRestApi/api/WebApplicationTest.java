package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class WebApplicationTest {

    private static final String REQUEST_URI = "https://gorest.co.in/public/v2/users";
    private static final String accessToken = "4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8";
    @MockBean
    private RestTemplate restTemplateMock;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @Value("${client.id}")
    private String ID;
    @Value("${client.secret}")
    private String secret;


    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "active");
        HttpHeaders headers = prepareHeaders();
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        when(restTemplateMock.exchange(REQUEST_URI, HttpMethod.POST, entity, User.class)).thenReturn(new ResponseEntity<>(user, HttpStatus.CREATED));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .header("clientId", ID)
                .header("clientSecret", secret)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(user));

        // act & assert
        mockMvc.perform(mockRequest).andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("John")))
                .andExpect(jsonPath("$.gender", is("male")));;
    }

    @Test
    public void shouldReturnStatusCode400WhenRegisteringUserWithInvalidGenderValue() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "mal", "active");
        HttpHeaders headers = prepareHeaders();
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        when(restTemplateMock.exchange(REQUEST_URI, HttpMethod.POST, entity, User.class)).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

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
    public void shouldReturnStatusCode400WhenRegisteringUserWithInvalidStatusValue() throws Exception {
        // arrange
        User user = new User(-1L,"John", "jo@gmail.com", "male", "old member");
        HttpHeaders headers = prepareHeaders();
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        when(restTemplateMock.exchange(REQUEST_URI, HttpMethod.POST, entity, User.class)).thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

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
        HttpHeaders headers = prepareHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/-1", HttpMethod.DELETE, entity, Void.class)).thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete("/users/-1")
                .header("clientId", ID)
                .header("clientSecret", secret)
                .contentType(MediaType.APPLICATION_JSON);

        // act & assert
        mockMvc.perform(mockRequest).andExpect(status().isNoContent());
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/-1", HttpMethod.DELETE, entity, Void.class);
    }

    @Test
    public void shouldCreateTODOSuccessfully() throws Exception {
        // arrange
        TODOItem todo = new TODOItem(-1L, -1L, "2022", "2023-01-01", "pending");
        HttpHeaders headers = prepareHeaders();
        HttpEntity<TODOItem> entity = new HttpEntity<>(todo, headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + todo.getUserId() + "/todos", HttpMethod.POST, entity, TODOItem.class)).thenReturn(new ResponseEntity<>(todo, HttpStatus.CREATED));

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
    public void shouldGetTODOSuccessfully() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem thirdTODO = new TODOItem(-3L, userId, "Groceries", "2022-03-16", "completed");
        TODOItem[] todos = {firstTODO, secondTODO, thirdTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));


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
        TODOItem[] todos = {firstTODO, secondTODO, thirdTODO, fourthTODO, fifthTODO, sixthTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

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
    public void shouldGetTwoTODOsFilteredWithTitleAndStatus() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem secondTODO = new TODOItem(-2L, userId, "CV for job", "2022-06-30", "completed");
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem[] todos = {secondTODO, fourthTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("title", "job")
                                .param("status", "completed"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(2)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(secondTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].due_on", is(secondTODO.getDue_on())))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].status", is(secondTODO.getStatus())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(fourthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].due_on", is(fourthTODO.getDue_on())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].status", is(fourthTODO.getStatus())));
    }

    @Test
    public void shouldGetSpecifiedTODOFilteredWithTitle() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem secondTODO = new TODOItem(-2L, userId, "CV for job", "2022-06-30", "completed");
        TODOItem fourthTODO = new TODOItem(-3L, userId, "Job Application", "2021-03-16", "completed");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        TODOItem[] todos = {secondTODO, fourthTODO, sixthTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("title", "job"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(3)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(secondTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].status", is(secondTODO.getStatus())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(fourthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].status", is(fourthTODO.getStatus())))
                        .andExpect(jsonPath("_embedded.tODOItemList[2].title", is(sixthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[2].status", is(sixthTODO.getStatus())));
    }

    @Test
    public void shouldGetSpecifiedTODOFilteredWithStatus() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        TODOItem[] todos = {firstTODO, sixthTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", secret)
                                .param("status", "pending"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("_embedded.tODOItemList", hasSize(2)))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].title", is(firstTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[0].status", is(firstTODO.getStatus())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].title", is(sixthTODO.getTitle())))
                        .andExpect(jsonPath("_embedded.tODOItemList[1].status", is(sixthTODO.getStatus())));
    }

    @Test
    public void shouldThrowResponseStatusExceptionWhenCalledWithInvalidClientId() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        TODOItem[] todos = {firstTODO, sixthTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", "182AJM83MA840HV")
                                .header("clientSecret", secret)
                                .param("status", "pending"))
                        .andExpect(status().isUnauthorized());
    }

    @Test
    public void shouldThrowResponseStatusExceptionWhenCalledWithInvalidClientSecret() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem sixthTODO = new TODOItem(-3L, userId, "Job", "2024-05-06", "pending");
        TODOItem[] todos = {firstTODO, sixthTODO};
        HttpHeaders headers = prepareHeaders();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act & assert
        ResultActions result =
                mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                                .header("clientId", ID)
                                .header("clientSecret", "182AJM83MA840HV")
                                .param("status", "pending"))
                        .andExpect(status().isUnauthorized());
    }

    public HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}
