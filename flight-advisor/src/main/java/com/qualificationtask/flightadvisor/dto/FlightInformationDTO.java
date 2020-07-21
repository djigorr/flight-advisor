package com.qualificationtask.flightadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class FlightInformationDTO {

    private String sourceCity;
    private String destinationCity;
    private List<RouteDTO> routes = new ArrayList<>();
    private Float totalPrice;
    private Float length;
}
