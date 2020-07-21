package com.qualificationtask.flightadvisor.service;

import static org.junit.jupiter.api.Assertions.*;

import com.qualificationtask.flightadvisor.domain.*;
import com.qualificationtask.flightadvisor.repository.AirportRepository;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.RouteRepository;
import com.qualificationtask.flightadvisor.utils.CSVHelper;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataImportServiceTest {

    @InjectMocks
    private DataImportService dataImportService;

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    CSVHelper helper;

    @Test
    public void testSaveAirportsSuccessful() throws IOException{
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).id(1L).build();
        Airport airport = Airport.builder().city(city).country("Serbia").id(1L).build();
        MultipartFile multipartFile = new MockMultipartFile("airports", "airports.txt", MediaType.TEXT_PLAIN_VALUE, IOUtils.toByteArray(new ClassPathResource("airports.txt").getInputStream()));
        when(helper.hasCSVFormat(any(MultipartFile.class))).thenReturn(true);
        when(cityRepository.findAll()).thenReturn(Arrays.asList(city));
        when(helper.csvToAirports(any(InputStream.class), anyList())).thenReturn(Arrays.asList(airport));

        String message = dataImportService.saveAirports(multipartFile);

        verify(cityRepository, times(1)).findAll();
        verify(airportRepository, times(1)).saveAll(anyList());

        assertEquals(message, "Airports data uploaded successfully.");
    }

    @Test
    public void testSaveRoutesSuccessful() throws IOException{
        City city = City.builder().name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).id(1L).build();
        Airport airport = Airport.builder().city(city).country("Serbia").id(1L).build();
        Route route = Route.builder().airline("Airline").sourceAirport(airport).destinationAirport(airport).price(25.5f).build();
        MultipartFile multipartFile = new MockMultipartFile("routes", "routes.txt", MediaType.TEXT_PLAIN_VALUE, IOUtils.toByteArray(new ClassPathResource("airports.txt").getInputStream()));
        when(helper.hasCSVFormat(any(MultipartFile.class))).thenReturn(true);
        when(airportRepository.findAll()).thenReturn(Arrays.asList(airport));
        when(helper.csvToRoutes(any(InputStream.class), anyList())).thenReturn(Arrays.asList(route));

        String message = dataImportService.saveRoutes(multipartFile);

        verify(airportRepository, times(1)).findAll();
        verify(routeRepository, times(1)).deleteAll();
        verify(routeRepository, times(1)).saveAll(anyList());

        assertEquals(message, "Routes data uploaded successfully.");
    }

    @Test
    public void testSaveAirportsNoCitiesFail() throws IOException{

        MultipartFile multipartFile = new MockMultipartFile("airports", "airports.txt", MediaType.TEXT_PLAIN_VALUE, IOUtils.toByteArray(new ClassPathResource("airports.txt").getInputStream()));
        when(helper.hasCSVFormat(any(MultipartFile.class))).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> dataImportService.saveAirports(multipartFile));

        verify(helper, times(1)).hasCSVFormat(any(MultipartFile.class));
        verify(cityRepository, times(1)).findAll();
        verify(helper, times(0)).csvToAirports(any(InputStream.class), anyList());
        verify(airportRepository, times(0)).saveAll(anyList());

        assertEquals(exception.getReason(),"There are no cities saved in database. Please add cities first.");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void testSaveRoutesNoAirportsFail() throws IOException{

        MultipartFile multipartFile = new MockMultipartFile("routes", "routes.txt", MediaType.TEXT_PLAIN_VALUE, IOUtils.toByteArray(new ClassPathResource("airports.txt").getInputStream()));
        when(helper.hasCSVFormat(any(MultipartFile.class))).thenReturn(true);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> dataImportService.saveRoutes(multipartFile));

        verify(helper, times(1)).hasCSVFormat(any(MultipartFile.class));
        verify(airportRepository, times(1)).findAll();
        verify(helper, times(0)).csvToRoutes(any(InputStream.class), anyList());
        verify(routeRepository, times(0)).deleteAll();
        verify(routeRepository, times(0)).saveAll(anyList());

        assertEquals(exception.getReason(),"There are no airports saved in database. Please import airports first.");
        assertEquals(exception.getStatus(), HttpStatus.BAD_REQUEST);
    }
}