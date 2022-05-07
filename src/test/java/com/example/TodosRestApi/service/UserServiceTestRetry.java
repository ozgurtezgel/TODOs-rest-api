package com.example.TodosRestApi.service;

import com.example.TodosRestApi.model.TODOItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class UserServiceTestRetry {

    private static final String REQUEST_URI = "https://gorest.co.in/public/v2/users";
    private static final String accessToken = "4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8";
    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private MockMvc mockMvc;
    @Value("${client.id}")
    private String ID;
    @Value("${client.secret}")
    private String secret;


    @Test
    public void tryGettingTodosThreeTimesWhenFailed() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        when(restTemplate.exchange(REQUEST_URI + "/-1/todos", HttpMethod.GET, entity, TODOItem[].class)).thenThrow(RestClientException.class);

        try {
            ResultActions result =
                    mockMvc.perform(get("/users/-1/todos").contentType(MediaType.APPLICATION_JSON)
                            .header("clientId", ID)
                            .header("clientSecret", secret));
        } catch (Exception e) {
        }
        verify(restTemplate, times(3)).exchange(REQUEST_URI + "/-1/todos", HttpMethod.GET, entity, TODOItem[].class);
    }
}
