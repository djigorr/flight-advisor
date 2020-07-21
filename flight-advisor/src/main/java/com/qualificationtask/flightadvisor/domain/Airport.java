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
public class Airport {

    @Id
    private Long id;

    private String name;

    @JoinColumn(name="city_id")
    @ManyToOne()
    private City city;

    private String country;

    @Column(length = 3)
    private String iata;

    @Column(length = 4)
    private String icao;

    private double latitude;

    private double longitude;

    private int altitude;

    private int timezone;

    @Column(length = 1)
    private String dst;

    private String tz;

    private String type;

    private String source;
}
