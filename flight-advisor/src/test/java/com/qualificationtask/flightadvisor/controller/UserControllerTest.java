package com.qualificationtask.flightadvisor.controller;

import com.qualificationtask.flightadvisor.dto.LoginDTO;
import com.qualificationtask.flightadvisor.dto.UserDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void testRegisterSuccessful() {
        UserDTO user = UserDTO.builder().firstName("Igor").lastName("Djukic").username("igi").password("abc").build();

        ResponseEntity<UserDTO> responseEntity = restTemplate.postForEntity("/api/register", user, UserDTO.class);
        UserDTO saved = responseEntity.getBody();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Igor", saved.getFirstName());
        assertEquals("Djukic", saved.getLastName());
        assertEquals("igi", saved.getUsername());
        assertEquals("USER", saved.getRole());
    }

    @Test
    public void testRegisterUserWithOmittedPasswordFail() {
        UserDTO user = UserDTO.builder().firstName("Igor").lastName("Djukic").username("igi").build();

        ResponseEntity<UserDTO> responseEntity = restTemplate.postForEntity("/api/register", user, UserDTO.class);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    public void testLoginSuccessful() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/login", new LoginDTO("Admin","123"), String.class);
        String token = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(token);
    }

    @Test
    public void testLoginWithIncorrectCredentialsFail() {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/login", new LoginDTO("Admin","000"), String.class);

        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
    }
}