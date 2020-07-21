package com.qualificationtask.flightadvisor.dto.mapper;

import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.dto.CityDTO;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CityMapper {

    public static City dtoToCity(CityDTO dto){
        return City.builder()
                .name(dto.getName())
                .country(dto.getCountry())
                .description(dto.getDescription())
                .build();
    }

    public static CityDTO cityToDto(City city){
        return CityDTO.builder()
                .name(city.getName())
                .country(city.getCountry())
                .description(city.getDescription())
                .id(city.getId())
                .comments(city.getComments().isEmpty()? new ArrayList<>() : city.getComments().stream().map(CommentMapper::commentToDto).collect(Collectors.toList()))
                .build();
    }
}
