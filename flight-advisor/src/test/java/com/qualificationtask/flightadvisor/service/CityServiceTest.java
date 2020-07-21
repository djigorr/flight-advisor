package com.qualificationtask.flightadvisor.service;

import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Comment;
import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.dto.CityDTO;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSuccessful(){
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        CityDTO dto = CityDTO.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        when(cityRepository.findByNameEqualsIgnoringCase(anyString())).thenReturn(Optional.empty());
        when(cityRepository.save(any(City.class))).thenReturn(city);

        CityDTO storedCity = cityService.create(dto);

        verify(cityRepository, times(1)).findByNameEqualsIgnoringCase(anyString());
        verify(cityRepository, times(1)).save(any(City.class));

        assertEquals(storedCity.getName(), dto.getName());
        assertEquals(storedCity.getCountry(), dto.getCountry());
        assertEquals(storedCity.getDescription(), dto.getDescription());
        assertTrue(storedCity.getComments().isEmpty());
    }

    @Test
    public void testCreateFail() {
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        CityDTO dto = CityDTO.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        when(cityRepository.findByNameEqualsIgnoringCase(anyString())).thenReturn(Optional.of(city));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cityService.create(dto));

        verify(cityRepository, times(1)).findByNameEqualsIgnoringCase(anyString());
        verify(cityRepository, times(0)).save(any(City.class));

        assertEquals(exception.getStatus(),HttpStatus.BAD_REQUEST);
        assertEquals(exception.getReason(),"City with name " + dto.getName() + " already exists.");
    }

    @Test
    public void testFindAllCitiesWithAllCommentsSuccessful() {
        Comment comment = Comment.builder().description("comment").createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).user(new User()).build();
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(Arrays.asList(comment)).build();

        when(cityRepository.findAll()).thenReturn(Arrays.asList(city));

        List<CityDTO> cities = cityService.findAll(null);

        verify(cityRepository, times(1)).findAll();

        assertTrue(cities.size() == 1);
        assertEquals(cities.get(0).getName(), "Novi Sad");
        assertEquals(cities.get(0).getCountry(), "Serbia");
        assertEquals(cities.get(0).getDescription(), "Nice city");
        assertTrue(cities.get(0).getComments().size() == 1);
        assertEquals(cities.get(0).getComments().get(0).getDescription(), "comment");
    }

    @Test
    public void testFindAllCitiesWithLimitedCommentsSuccessful() {
        Comment comment1 = Comment.builder().description("first").createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).user(new User()).build();
        Comment comment2 = Comment.builder().description("second").createdDate(LocalDateTime.now().plusDays(1)).modifiedDate(LocalDateTime.now().plusDays(1)).user(new User()).build();
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(Arrays.asList(comment1, comment2)).build();

        when(cityRepository.findAll()).thenReturn(Arrays.asList(city));

        List<CityDTO> cities = cityService.findAll(1);

        verify(cityRepository, times(1)).findAll();

        assertTrue(cities.size() == 1);
        assertEquals(cities.get(0).getName(), "Novi Sad");
        assertEquals(cities.get(0).getCountry(), "Serbia");
        assertEquals(cities.get(0).getDescription(), "Nice city");
        assertTrue(cities.get(0).getComments().size() == 1);
        assertEquals(cities.get(0).getComments().get(0).getDescription(),"second");
    }

    @Test
    public void testFindByNameWithAllCommentsSuccessful() {
        Comment comment1 = Comment.builder().description("first").createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).user(new User()).build();
        Comment comment2 = Comment.builder().description("second").createdDate(LocalDateTime.now().plusDays(1)).modifiedDate(LocalDateTime.now().plusDays(1)).user(new User()).build();
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(Arrays.asList(comment1, comment2)).build();

        when(cityRepository.findByNameEqualsIgnoringCase("Novi Sad")).thenReturn(Optional.of(city));

        CityDTO found = cityService.findByName("Novi Sad", null);

        verify(cityRepository, times(1)).findByNameEqualsIgnoringCase(anyString());

        assertEquals(found.getName(), city.getName());
        assertEquals(found.getCountry(), city.getCountry());
        assertEquals(found.getDescription(), city.getDescription());
        assertEquals(found.getComments().size(), city.getComments().size());
    }

    @Test
    public void testFindByNameWithLimitedCommentsSuccessful() {
        Comment comment1 = Comment.builder().description("first").createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).user(new User()).build();
        Comment comment2 = Comment.builder().description("second").createdDate(LocalDateTime.now().plusDays(1)).modifiedDate(LocalDateTime.now().plusDays(1)).user(new User()).build();
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(Arrays.asList(comment1, comment2)).build();

        when(cityRepository.findByNameEqualsIgnoringCase("Novi Sad")).thenReturn(Optional.of(city));

        CityDTO found = cityService.findByName("Novi Sad", 1);

        verify(cityRepository, times(1)).findByNameEqualsIgnoringCase(anyString());

        assertEquals(found.getName(), city.getName());
        assertEquals(found.getCountry(), city.getCountry());
        assertEquals(found.getDescription(), city.getDescription());
        assertTrue(found.getComments().size() == 1);
        assertEquals(found.getComments().get(0).getDescription(),"second");
    }

    @Test
    public void testFindByNameFail() {
        Comment comment = Comment.builder().description("comment").createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).user(new User()).build();
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(Arrays.asList(comment)).build();

        when(cityRepository.findByNameEqualsIgnoringCase("Novi Sad")).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> cityService.findByName("Novi Sad", null));

        verify(cityRepository, times(1)).findByNameEqualsIgnoringCase(anyString());

        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
        assertEquals(exception.getReason(),"City with name Novi Sad does not exist.");
    }
}