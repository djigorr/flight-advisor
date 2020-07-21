package com.qualificationtask.flightadvisor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RouteDTO {

    private String sourceCity;
    private String destinationCity;
    private Float price;
}
