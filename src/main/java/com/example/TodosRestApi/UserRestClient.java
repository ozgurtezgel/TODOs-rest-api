package com.example.TodosRestApi;


import com.example.TodosRestApi.model.TODOItem;
import com.example.TodosRestApi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class UserRestClient {

    private static final String REQUEST_URI = "https://gorest.co.in/public/v2/users";
    @Value("${access.token}")
    private String accessToken;
    @Lazy
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestClient.class);

    public UserRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public User registerUser(User user) {
        HttpHeaders headers = prepareHeader();
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<User> responseEntity = restTemplate.exchange(REQUEST_URI, HttpMethod.POST, entity, User.class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
        LOGGER.info("Body: " + responseEntity.getBody());
//        System.out.println(responseEntity.getBody().toString());
        return responseEntity.getBody();
    }

    public void deleteUserByID(Long id) {

        HttpHeaders headers = prepareHeader();
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + id, HttpMethod.DELETE, entity, Void.class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
    }

    public List<TODOItem> getTODOs(Long id) {
        HttpHeaders headers = prepareHeader();
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        String url = REQUEST_URI + "/" + id + "/todos";
        System.out.println(url);
        ResponseEntity<TODOItem[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, TODOItem[].class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
        System.out.println(responseEntity.getBody().length);
        return Arrays.stream(responseEntity.getBody()).toList();
    }

    public TODOItem createTODO(TODOItem todo, Long id) {
        HttpHeaders headers = prepareHeader();
        HttpEntity<TODOItem> entity = new HttpEntity<>(todo, headers);
        ResponseEntity<TODOItem> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + id + "/todos", HttpMethod.POST, entity, TODOItem.class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
//        System.out.println(responseEntity.getBody().toString());
        return responseEntity.getBody();
    }

    public HttpHeaders prepareHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Authorization", "Bearer 4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8");
        return headers;
    }
}
