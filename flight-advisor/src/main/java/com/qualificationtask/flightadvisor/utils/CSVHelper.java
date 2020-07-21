package com.qualificationtask.flightadvisor.utils;

import com.qualificationtask.flightadvisor.domain.Airport;
import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Route;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CSVHelper {

    public String TYPE = "text/plain";

    public boolean hasCSVFormat(MultipartFile file){
        return file.getContentType().equals(TYPE);
    }

    public List<Airport> csvToAirports(InputStream is, List<City> cities){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT.withEscape('\\'));
            List<Airport> airports = new ArrayList<>();
            List<CSVRecord> csvRecords = csvParser.getRecords();
            for(CSVRecord csvRecord : csvRecords){
                Optional<City> city = cities.stream().filter(c->c.getName().equals(csvRecord.get(2))).findAny();
                if(!city.isPresent()) continue;
                Airport airport = new Airport(
                        Long.parseLong(csvRecord.get(0)),
                        csvRecord.get(1),
                        city.get(),
                        csvRecord.get(3),
                        csvRecord.get(4),
                        csvRecord.get(5),
                        Double.parseDouble(csvRecord.get(6)),
                        Double.parseDouble(csvRecord.get(7)),
                        Integer.parseInt(csvRecord.get(8)),
                        Integer.parseInt(csvRecord.get(9)),
                        csvRecord.get(10),
                        csvRecord.get(11),
                        csvRecord.get(12),
                        csvRecord.get(13)
                );
                airports.add(airport);
            }
            return airports;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file.");
        }
    }

    public List<Route> csvToRoutes(InputStream is, List<Airport> airports){
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            CSVParser csvParser = new CSVParser(br, CSVFormat.DEFAULT);

            List<Route> routes = new ArrayList<>();
            List<CSVRecord> csvRecords = csvParser.getRecords();
            for(CSVRecord csvRecord : csvRecords){
                Optional<Airport> sourceAirport = airports.stream().filter(a->a.getIata().equals(csvRecord.get(2))).findAny();
                Optional<Airport> destinationAirport = airports.stream().filter(a->a.getIata().equals(csvRecord.get(4))).findAny();
                if(!sourceAirport.isPresent() || !destinationAirport.isPresent()) continue;
                Route route = new Route(
                        null,
                        csvRecord.get(0),
                        csvRecord.get(1),
                        sourceAirport.get(),
                        destinationAirport.get(),
                        csvRecord.get(6).isEmpty()? Character.MIN_VALUE : csvRecord.get(6).charAt(0),
                        Integer.parseInt(csvRecord.get(7)),
                        csvRecord.get(8),
                        Float.parseFloat(csvRecord.get(9))
                );
                routes.add(route);
            }
            return routes;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file.");
        }
    }
}
