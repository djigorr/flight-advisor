package com.qualificationtask.flightadvisor.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 3)
    private String airline;

    private String airlineId;

    @JoinColumn(name="source_airport_id")
    @OneToOne
    private Airport sourceAirport;

    @JoinColumn(name="destination_airport_id")
    @OneToOne
    private Airport destinationAirport;

    @Column(length = 1)
    private char codeshare;

    private Integer stops;

    private String equipment;

    private Float price;
}
