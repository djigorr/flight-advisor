package com.qualificationtask.flightadvisor.utils;

import com.qualificationtask.flightadvisor.domain.Airport;
import com.qualificationtask.flightadvisor.domain.City;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class Graph {

    private Set<Node> nodes = new HashSet<>();

    public void addNode(Node nodeA) {
        nodes.add(nodeA);
    }

    public Node findByAirport(Airport airport){
        return nodes.stream().filter(n->n.getAirport().equals(airport)).findAny().orElse(null);
    }

    public List<Node> findByCity(City city){
        return nodes.stream().filter(n->n.getAirport().getCity().getName().equals(city.getName())).collect(Collectors.toList());
    }
}
