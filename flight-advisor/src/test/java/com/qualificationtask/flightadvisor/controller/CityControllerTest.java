package com.qualificationtask.flightadvisor.controller;

import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Comment;
import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.dto.CityDTO;
import com.qualificationtask.flightadvisor.dto.CommentDTO;
import com.qualificationtask.flightadvisor.dto.LoginDTO;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.CommentRepository;
import com.qualificationtask.flightadvisor.repository.UserRepository;
import com.qualificationtask.flightadvisor.service.CityService;
import com.qualificationtask.flightadvisor.service.CommentService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CityControllerTest {

    @Autowired
    CityService cityService;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TestRestTemplate restTemplate;

    private String accessToken;
    private Long cityId;
    private Long commentId;

    @Before
    public void login(){
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/login", new LoginDTO("Admin","123"), String.class);
        accessToken = responseEntity.getBody();
        initializeData();
    }

    @Test
    public void testAddCity()  {
        final CityDTO dto = CityDTO.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<CityDTO> httpEntity = new HttpEntity<>(dto, headers);
        ResponseEntity<CityDTO> responseEntity = restTemplate.exchange("/api/cities", HttpMethod.POST, httpEntity, CityDTO.class);
        CityDTO city = responseEntity.getBody();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("Novi Sad", city.getName());
        assertEquals("Serbia", city.getCountry());
        assertEquals("Nice city", city.getDescription());
    }

    @Test
    public void testFindCityByName() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<CityDTO> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<CityDTO> responseEntity = restTemplate.exchange("/api/cities/Nis", HttpMethod.GET, httpEntity, CityDTO.class);
        CityDTO city = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Nis", city.getName());
        assertEquals("Serbia", city.getCountry());
        assertEquals("Nice city", city.getDescription());
    }

    @Test
    public void testGetAllCities() {

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<CityDTO[]> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<CityDTO[]> responseEntity = restTemplate.exchange("/api/cities?comments=2", HttpMethod.GET, httpEntity, CityDTO[].class);
        CityDTO[] cities = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(cities.length > 0);
    }

    @Test
    public void testAddComment() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String content = "test comment";
        HttpEntity<String> httpEntity = new HttpEntity<>(content, headers);
        ResponseEntity<CommentDTO> responseEntity = restTemplate.exchange("/api/cities/" +cityId + "/comments", HttpMethod.POST, httpEntity, CommentDTO.class);
        CommentDTO comment = responseEntity.getBody();

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("test comment", comment.getDescription());
    }

    @Test
    public void testUpdateComment() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        String content = "test comment";
        HttpEntity<String> httpEntity = new HttpEntity<>(content, headers);
        ResponseEntity<CommentDTO> responseEntity = restTemplate.exchange("/api/cities/" + cityId + "/comments/" + commentId, HttpMethod.PUT, httpEntity, CommentDTO.class);
        CommentDTO comment = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("test comment", comment.getDescription());
    }

    @Test
    public void testRemoveComment() {
        int commentsBefore = commentRepository.findByCityId(cityId).size();

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Void> responseEntity = restTemplate.exchange("/api/cities/" + cityId + "/comments/" + commentId, HttpMethod.DELETE, httpEntity, Void.class);

        int commentsAfter = commentRepository.findByCityId(cityId).size();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(commentsBefore -1 , commentsAfter);
    }

    @After
    public void dataCleanup() {
        commentRepository.deleteAll();
        cityRepository.deleteAll();
    }

    private void initializeData(){
        User admin = userRepository.findByUsername("Admin").get();
        City city = City.builder().name("Nis").country("Serbia").description("Nice city").build();
        City savedCity = cityRepository.save(city);
        cityId = savedCity.getId();
        Comment comment = Comment.builder().description("testing").city(savedCity).user(admin).build();
        Comment savedComment = commentRepository.save(comment);
        commentId = savedComment.getId();
    }
}