package com.qualificationtask.flightadvisor.service;

import com.qualificationtask.flightadvisor.domain.Airport;
import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Route;
import com.qualificationtask.flightadvisor.dto.FlightInformationDTO;
import com.qualificationtask.flightadvisor.dto.RouteDTO;
import com.qualificationtask.flightadvisor.repository.AirportRepository;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.RouteRepository;
import com.qualificationtask.flightadvisor.utils.DistanceCalculator;
import com.qualificationtask.flightadvisor.utils.Graph;
import com.qualificationtask.flightadvisor.utils.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Transactional
@Service
public class RouteSearchService {

    @Autowired
    RouteRepository routeRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    AirportRepository airportRepository;

    Graph graph;

    public FlightInformationDTO searchCheapestFlightBetweenCities(String sourceCityName, String destinationCityName){

        if(sourceCityName == null || destinationCityName == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please provide source and destination city names.");
        }
        City sourceCity = cityRepository.findByNameEqualsIgnoringCase(sourceCityName).<RuntimeException>orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Source city not found.");
        });
        City destinationCity = cityRepository.findByNameEqualsIgnoringCase(destinationCityName).<RuntimeException>orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination city not found.");
        });

        if(graph == null) {
            initializeGraph();
        }
        List<Node> sources = graph.findByCity(sourceCity);
        List<Node> destinations = graph.findByCity(destinationCity);
        if(destinations.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No routes were found for specified destination.");
        }

        FlightInformationDTO flightInformation = FlightInformationDTO.builder()
                .sourceCity(sourceCity.getName())
                .destinationCity(destinationCity.getName()).build();
        Float minimalPrice = Float.MAX_VALUE;

        for(Node source : sources){
            for(Node destination : destinations){
                resetGraphState();
                Node dest = calculateCheapestPath(source, destination);
                if(dest.getCheapestPath().isEmpty()){
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No routes were found for specified destination.");
                }
                if(dest.getPrice()<minimalPrice){
                    minimalPrice = dest.getPrice();
                    flightInformation.setTotalPrice(dest.getPrice());
                    flightInformation.setRoutes(new ArrayList<>());
                    List<Node> cheapestPath = dest.getCheapestPath();
                    for(int i=0; i<cheapestPath.size();i++){
                        RouteDTO route = new RouteDTO();
                        route.setSourceCity(cheapestPath.get(i).getAirport().getCity().getName());
                        if(i > 0){
                            flightInformation.getRoutes().get(i-1).setDestinationCity(cheapestPath.get(i).getAirport().getCity().getName());
                            flightInformation.getRoutes().get(i-1).setPrice(cheapestPath.get(i).getPrice() - cheapestPath.get(i-1).getPrice());
                        }
                        if(i == cheapestPath.size()-1){
                            route.setDestinationCity(destinationCity.getName());
                            route.setPrice(flightInformation.getTotalPrice() - cheapestPath.get(i).getPrice());
                        }
                        flightInformation.getRoutes().add(route);
                    }
                    flightInformation.setLength(calculateTotalLength(dest));
                }
            }
        }
        return flightInformation;
    }

    private Node calculateCheapestPath(Node source, Node destination) {
        source.setPrice(0f);

        Set<Node> settledNodes = new HashSet<>();
        Set<Node> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            Node currentNode = getLowestPriceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry< Node, Float> adjacencyPair: currentNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Float edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumPrice(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
        return destination;
    }

    private Node getLowestPriceNode(Set<Node> unsettledNodes) {
        Node lowestPriceNode = null;
        float lowestPrice = Float.MAX_VALUE;
        for (Node node: unsettledNodes) {
            float nodePrice = node.getPrice();
            if (nodePrice < lowestPrice) {
                lowestPrice = nodePrice;
                lowestPriceNode = node;
            }
        }
        return lowestPriceNode;
    }

    private void calculateMinimumPrice(Node evaluationNode, Float edgeWeigh, Node sourceNode) {
        Float sourcePrice = sourceNode.getPrice();
        if (sourcePrice + edgeWeigh < evaluationNode.getPrice()) {
            evaluationNode.setPrice(sourcePrice + edgeWeigh);
            LinkedList<Node> cheapestPath = new LinkedList<>(sourceNode.getCheapestPath());
            cheapestPath.add(sourceNode);
            evaluationNode.setCheapestPath(cheapestPath);
        }
    }

    private float calculateTotalLength(Node destination){
        float totalDistance = 0;
        List<Node> stops = destination.getCheapestPath();
        for(int i=0;i<stops.size();i++){
            if(i == stops.size()-1){
                totalDistance += DistanceCalculator.calculateDistance(stops.get(i).getAirport().getLatitude(), stops.get(i).getAirport().getLongitude(), destination.getAirport().getLatitude(), destination.getAirport().getLongitude());
            }
            if (i>0) {
                totalDistance += DistanceCalculator.calculateDistance(stops.get(i-1).getAirport().getLatitude(), stops.get(i-1).getAirport().getLongitude(), stops.get(i).getAirport().getLatitude(), stops.get(i).getAirport().getLongitude());
            }
        }
        return totalDistance;
    }

    private void initializeGraph(){

        graph = new Graph();
        List<Route> routes = routeRepository.findAll();

        for(Route route: routes){
            Airport sourceAirport = route.getSourceAirport();
            Airport destinationAirport = route.getDestinationAirport();

            Node destinationNode = graph.findByAirport(destinationAirport);
            if(destinationNode == null){
                destinationNode = new Node(destinationAirport);
                graph.addNode(destinationNode);
            }
            Node sourceNode = graph.findByAirport(sourceAirport);
            if(sourceNode == null){
                sourceNode = new Node(sourceAirport);
                sourceNode.addDestination(destinationNode, route.getPrice());
                graph.addNode(sourceNode);
            }else{
                if(!sourceNode.getAdjacentNodes().containsKey(destinationNode)){
                    sourceNode.addDestination(destinationNode, route.getPrice());
                }else{
                    Float price = sourceNode.getAdjacentNodes().get(destinationNode);
                    if(price > route.getPrice()){
                        sourceNode.getAdjacentNodes().put(destinationNode, route.getPrice());
                    }
                }
            }
        }
    }

    private void resetGraphState(){
        for(Node node : graph.getNodes()){
            node.setCheapestPath(new LinkedList<>());
            node.setPrice(Float.MAX_VALUE);
        }
    }
}
