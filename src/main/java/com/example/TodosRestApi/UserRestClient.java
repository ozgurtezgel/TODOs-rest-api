package com.example.TODOsRestApi;


import com.example.TODOsRestApi.model.TODO;
import com.example.TODOsRestApi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserRestClient {

    private static final String REQUEST_URI = "https://gorest.co.in/public/v2/users";
    // private static final String accessToken = "4140ef1db63d80c58651e1de7843aaa812f2470c7319be9e053bcd46578267e8";
    @Autowired
    @Lazy
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRestClient.class);

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public User registerUser(User user, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        HttpEntity<User> entity = new HttpEntity<>(user, headers);
        ResponseEntity<User> responseEntity = restTemplate.exchange(REQUEST_URI, HttpMethod.POST, entity, User.class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
        LOGGER.info("HTTP Header 'ContentType': " + responseEntity.getHeaders().getContentType());
//        System.out.println(responseEntity.getBody().toString());
        return responseEntity.getBody();
    }

    public void deleteUserByID(Long id, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<Void> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + id, HttpMethod.DELETE, entity, Void.class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
        LOGGER.info("HTTP Header 'ContentType': " + responseEntity.getHeaders().getContentType());
    }

    public TODO[] getTODOs(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>("body", headers);
        ResponseEntity<TODO[]> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + id + "/todos", HttpMethod.GET, entity, TODO[].class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
        LOGGER.info("HTTP Header 'ContentType': " + responseEntity.getHeaders().getContentType());
//        System.out.println(responseEntity.getBody().length);
        return responseEntity.getBody();
    }

    public TODO createTODO(TODO todo, Long id, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken);
        HttpEntity<TODO> entity = new HttpEntity<>(todo, headers);
        ResponseEntity<TODO> responseEntity = restTemplate.exchange(REQUEST_URI + "/" + id + "/todos", HttpMethod.POST, entity, TODO.class);
        LOGGER.info("Status code value: " + responseEntity.getStatusCodeValue());
        LOGGER.info("HTTP Header 'ContentType': " + responseEntity.getHeaders().getContentType());
//        System.out.println(responseEntity.getBody().toString());
        return responseEntity.getBody();
    }
}
