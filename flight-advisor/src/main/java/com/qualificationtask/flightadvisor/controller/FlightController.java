package com.qualificationtask.flightadvisor.controller;

import com.qualificationtask.flightadvisor.dto.FlightInformationDTO;
import com.qualificationtask.flightadvisor.service.DataImportService;
import com.qualificationtask.flightadvisor.service.RouteSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api("Controller for exposing operations related to Flights via REST endpoint.")
@RequestMapping("/api/flight")
public class FlightController {

    @Autowired
    DataImportService dataImportService;

    @Autowired
    RouteSearchService routeSearchService;

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Imports list of airports from file.")
    @PostMapping("/data/airports")
    public ResponseEntity<String> importAirports(@RequestParam("file") MultipartFile file) {
        String message = dataImportService.saveAirports(file);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Imports list of routes from file.")
    @PostMapping("/data/routes")
    public ResponseEntity<String> importRoutes(@RequestParam("file") MultipartFile file) {
        String message = dataImportService.saveRoutes(file);
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation("Finds cheapest flight for given destination.")
    @GetMapping("/cheapest/from/{source}/to/{destination}")
    public ResponseEntity<FlightInformationDTO> findCheapestFlight(@PathVariable String source, @PathVariable String destination){
        FlightInformationDTO info = routeSearchService.searchCheapestFlightBetweenCities(source, destination);
        return ResponseEntity.status(HttpStatus.OK).body(info);
    }
}
