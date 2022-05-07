package com.example.TodosRestApi.api;

import com.example.TodosRestApi.model.TODOItem;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@AutoConfigureMockMvc
@SpringBootTest
public class WebApplicationCacheTest {

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
    public void shouldGetTODOsSuccessfullyFromCachedWhenInvokedMoreThanOnce() throws Exception {
        // arrange
        Long userId = -1L;
        TODOItem firstTODO = new TODOItem(-1L, userId, "Math Class", "2022-06-06", "pending");
        TODOItem secondTODO = new TODOItem(-2L, userId, "Sport", "2022-06-30", "pending");
        TODOItem[] todos = {firstTODO, secondTODO};
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplateMock.exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class)).thenReturn(new ResponseEntity<>(todos, HttpStatus.OK));

        // act & assert
        mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                .header("clientId", ID)
                .header("clientSecret", secret));

        // to verify the first interaction
        verify(restTemplateMock, times(1)).exchange(REQUEST_URI + "/" + userId + "/todos", HttpMethod.GET, entity, TODOItem[].class);
        // to reset the interactions
        reset(restTemplateMock);

        mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                .header("clientId", ID)
                .header("clientSecret", secret));
        verifyNoInteractions(restTemplateMock);
    }
}
