package com.qualificationtask.flightadvisor.utils;

import com.qualificationtask.flightadvisor.domain.Airport;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Node {

    private Airport airport;

    private List<Node> cheapestPath = new LinkedList<>();

    private Float price = Float.MAX_VALUE;

    private Map<Node, Float> adjacentNodes = new HashMap<>();

    public void addDestination(Node destination, float price) {
        adjacentNodes.put(destination, price);
    }

    public Node(Airport airport) {
        this.airport = airport;
    }
}
