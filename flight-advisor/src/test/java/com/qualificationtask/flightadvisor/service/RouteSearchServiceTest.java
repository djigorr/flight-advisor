package com.qualificationtask.flightadvisor.service;

import static org.junit.jupiter.api.Assertions.*;

import com.qualificationtask.flightadvisor.domain.*;
import com.qualificationtask.flightadvisor.dto.FlightInformationDTO;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.RouteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RouteSearchServiceTest {

    @Mock
    RouteRepository routeRepository;

    @Mock
    CityRepository cityRepository;

    @InjectMocks
    RouteSearchService routeSearchService;

    @Test
    public void testSearchCheapestFlightBetweenCitiesSuccessful() {
        City city1 = City.builder().name("Belgrade").country("Serbia").description("Capital").comments(new ArrayList<>()).id(1L).build();
        City city2 = City.builder().name("Sarajevo").country("BiH").description("Capital").comments(new ArrayList<>()).id(2L).build();
        Airport airport1 = Airport.builder().id(1L).latitude(44.8184013367).longitude(20.3090991974).name("Belgrade Nikola Tesla Airport").city(city1).build();
        Airport airport2 = Airport.builder().id(2L).latitude(43.82460021972656).longitude(18.331499099731445).name("Sarajevo International Airport").city(city2).build();
        Route route1 = Route.builder().sourceAirport(airport1).destinationAirport(airport2).price(57.47f).build();
        Route route2 = Route.builder().sourceAirport(airport1).destinationAirport(airport2).price(42.35f).build();

        when(cityRepository.findByNameEqualsIgnoringCase("Belgrade")).thenReturn(Optional.of(city1));
        when(cityRepository.findByNameEqualsIgnoringCase("Sarajevo")).thenReturn(Optional.of(city2));
        when(routeRepository.findAll()).thenReturn(Arrays.asList(route1, route2));

        FlightInformationDTO info = routeSearchService.searchCheapestFlightBetweenCities("Belgrade", "Sarajevo");

        verify(cityRepository, times(2)).findByNameEqualsIgnoringCase(anyString());
        verify(routeRepository).findAll();

        assertEquals(info.getTotalPrice(),42.35f);
        assertEquals(info.getSourceCity(),"Belgrade");
        assertEquals(info.getDestinationCity(),"Sarajevo");
        assertTrue(info.getLength()>0);
    }

    @Test
    public void testSearchCheapestFlightBetweenCitiesCityNotExistingFail() {

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> routeSearchService.searchCheapestFlightBetweenCities("Novi Sad", "Sarajevo"));

        verify(cityRepository, times(1)).findByNameEqualsIgnoringCase(anyString());

        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
        assertEquals(exception.getReason(),"Source city not found.");
    }
}