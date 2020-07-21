package com.qualificationtask.flightadvisor.service;

import com.qualificationtask.flightadvisor.domain.Airport;
import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Route;
import com.qualificationtask.flightadvisor.repository.AirportRepository;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.RouteRepository;
import com.qualificationtask.flightadvisor.utils.CSVHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@Transactional
@Service
public class DataImportService {

    @Autowired
    CityRepository cityRepository;

    @Autowired
    AirportRepository airportRepository;

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    RouteSearchService routeSearchService;

    @Autowired
    CSVHelper helper;

    public String saveAirports(MultipartFile file){
        if(!helper.hasCSVFormat(file)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Airports data should be in text format.");
        }
        List<City> cities = cityRepository.findAll();
        if(cities.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are no cities saved in database. Please add cities first.");
        }
        try {
            List<Airport> airports = helper.csvToAirports(file.getInputStream(), cities);
            airportRepository.saveAll(airports);
            return "Airports data uploaded successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CSV data.");
        }
    }

    public String saveRoutes(MultipartFile file){
        if(!helper.hasCSVFormat(file)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Routes data should be in text format.");
        }
        List<Airport> airports = airportRepository.findAll();
        if(airports.isEmpty()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "There are no airports saved in database. Please import airports first.");
        }
        try {
            routeRepository.deleteAll();
            List<Route> routes = helper.csvToRoutes(file.getInputStream(), airports);
            routeRepository.saveAll(routes);
            return "Routes data uploaded successfully.";
        } catch (IOException e) {
            throw new RuntimeException("Failed to store CSV data.");
        }
    }
}
