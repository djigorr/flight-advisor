package com.qualificationtask.flightadvisor.service;

import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.dto.CityDTO;
import com.qualificationtask.flightadvisor.dto.CommentDTO;
import com.qualificationtask.flightadvisor.dto.mapper.CityMapper;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class CityService {

    @Autowired
    CityRepository cityRepository;

    public CityDTO create(CityDTO dto){
        cityRepository.findByNameEqualsIgnoringCase(dto.getName()).ifPresent(c -> {throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City with name " + dto.getName() + " already exists.");});
        City savedCity = cityRepository.save(CityMapper.dtoToCity(dto));
        savedCity.setComments(new ArrayList<>());
        return CityMapper.cityToDto(savedCity);
    }

    public CityDTO findByName(String name, Integer comments){
        City city = cityRepository.findByNameEqualsIgnoringCase(name).<RuntimeException>orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City with name " + name + " does not exist.");
        });
        CityDTO dto = CityMapper.cityToDto(city);
        if(comments != null){
            dto.setComments(dto.getComments().stream().sorted(Comparator.comparing(CommentDTO::getCreatedDate).reversed()).limit(comments).collect(Collectors.toList()));
        }
        return dto;
    }

    public List<CityDTO> findAll(Integer comments){
        List<CityDTO> dtos = cityRepository.findAll().stream().map(CityMapper::cityToDto).collect(Collectors.toList());
        if(comments != null){
            dtos = dtos.stream().map(c->{
                c.setComments(c.getComments().stream().sorted(Comparator.comparing(CommentDTO::getCreatedDate).reversed()).limit(comments).collect(Collectors.toList()));
                return c;
            }).collect(Collectors.toList());
        }
        return dtos;
    }
}
