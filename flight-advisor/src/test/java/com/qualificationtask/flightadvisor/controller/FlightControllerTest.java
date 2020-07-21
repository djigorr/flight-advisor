package com.qualificationtask.flightadvisor.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.qualificationtask.flightadvisor.domain.Airport;
import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Route;
import com.qualificationtask.flightadvisor.dto.FlightInformationDTO;
import com.qualificationtask.flightadvisor.dto.LoginDTO;
import com.qualificationtask.flightadvisor.repository.*;
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
import org.springframework.util.LinkedMultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FlightControllerTest {

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

    @Autowired
    AirportRepository airportsRepository;

    @Autowired
    RouteRepository routeRepository;

    private String accessToken;

    @Before
    public void login(){
        ResponseEntity<String> responseEntity = restTemplate.postForEntity("/api/login", new LoginDTO("Admin","123"), String.class);
        accessToken = responseEntity.getBody();
    }

    @Test
    public void testImportAirportsSuccessful() {

        City city = City.builder().name("Belgrade").country("Serbia").description("Capital").build();
        cityRepository.save(city);

        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new org.springframework.core.io.ClassPathResource("airports.txt"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-Auth-Token", accessToken);
        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flight/data/airports", HttpMethod.POST, httpEntity, String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testImportRoutesSuccessful() {
        City city1 = City.builder().name("Belgrade").country("Serbia").description("Capital").build();
        City city2 = City.builder().name("Sarajevo").country("Bih").description("Capital").build();
        cityRepository.saveAll(Arrays.asList(city1, city2));
        Airport airport1 = Airport.builder().city(city1).country("Serbia").id(1L).iata("BEG").build();
        Airport airport2 = Airport.builder().city(city2).country("BiH").id(2L).iata("Sjj").build();
        airportsRepository.saveAll(Arrays.asList(airport1, airport2));

        LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("file", new org.springframework.core.io.ClassPathResource("routes.txt"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.add("X-Auth-Token", accessToken);
        HttpEntity<LinkedMultiValueMap<String, Object>> httpEntity = new HttpEntity<>(parameters, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange("/api/flight/data/routes", HttpMethod.POST, httpEntity, String.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testFindCheapestFlightSuccessful() {
        City city1 = City.builder().name("Belgrade").country("Serbia").description("Capital").comments(new ArrayList<>()).id(1L).build();
        City city2 = City.builder().name("Sarajevo").country("BiH").description("Capital").comments(new ArrayList<>()).id(2L).build();
        cityRepository.saveAll(Arrays.asList(city1, city2));
        Airport airport1 = Airport.builder().id(1L).latitude(44.8184013367).longitude(20.3090991974).name("Belgrade Nikola Tesla Airport").city(city1).build();
        Airport airport2 = Airport.builder().id(2L).latitude(43.82460021972656).longitude(18.331499099731445).name("Sarajevo International Airport").city(city2).build();
        airportsRepository.saveAll(Arrays.asList(airport1, airport2));
        Route route1 = Route.builder().sourceAirport(airport1).destinationAirport(airport2).price(57.47f).build();
        Route route2 = Route.builder().sourceAirport(airport1).destinationAirport(airport2).price(42.35f).build();
        routeRepository.saveAll(Arrays.asList(route1, route2));

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Auth-Token", accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<FlightInformationDTO> responseEntity = restTemplate.exchange("/api/flight/cheapest/from/Belgrade/to/Sarajevo", HttpMethod.GET, httpEntity, FlightInformationDTO.class);
        FlightInformationDTO info = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(info.getTotalPrice(),42.35f);
        assertEquals(info.getSourceCity(),"Belgrade");
        assertEquals(info.getDestinationCity(),"Sarajevo");
        assertTrue(info.getLength()>0);
    }

    @After
    public void dataCleanup() {
        commentRepository.deleteAll();
        routeRepository.deleteAll();
        airportsRepository.deleteAll();
        cityRepository.deleteAll();
    }
}