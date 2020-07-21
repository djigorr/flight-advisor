package com.qualificationtask.flightadvisor.controller;

import com.qualificationtask.flightadvisor.dto.CityDTO;
import com.qualificationtask.flightadvisor.dto.CommentDTO;
import com.qualificationtask.flightadvisor.service.CityService;
import com.qualificationtask.flightadvisor.service.CommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api("Controller for exposing City and Comment related operations via REST endpoint.")
@RequestMapping("/api/cities")
public class CityController {

    @Autowired
    CityService cityService;

    @Autowired
    CommentService commentService;

    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("Adds city to database.")
    @PostMapping()
    public ResponseEntity<CityDTO> addCity(@Valid @RequestBody CityDTO dto){
        CityDTO created = cityService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation("Returns city with specified name with all of its comments or specified number of latest comments.")
    @GetMapping("/{name}")
    public ResponseEntity<CityDTO> findCityByName(@PathVariable String name, @RequestParam(required = false) Integer comments){
        CityDTO city = cityService.findByName(name, comments);
        return ResponseEntity.status(HttpStatus.OK).body(city);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation("Returns all cities from database with all of its comments or specified number of latest comments.")
    @GetMapping()
    public ResponseEntity<List<CityDTO>> getAllCities(@RequestParam(required = false) Integer comments){
        List<CityDTO> cities = cityService.findAll(comments);
        return ResponseEntity.status(HttpStatus.OK).body(cities);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation("Adds comment for a given city.")
    @PostMapping("/{cityId}/comments")
    public ResponseEntity<CommentDTO> addComment(@RequestBody String description, @PathVariable Long cityId){
        CommentDTO created = commentService.create(description, cityId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation("Deletes comment of given city.")
    @DeleteMapping("/{cityId}/comments/{commentId}")
    public ResponseEntity<Void> removeComment(@PathVariable Long cityId, @PathVariable Long commentId){
        commentService.delete(cityId, commentId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PreAuthorize("isAuthenticated()")
    @ApiOperation("Updates comment for given city.")
    @PutMapping("/{cityId}/comments/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(@RequestBody String description, @PathVariable Long cityId, @PathVariable Long commentId){
        CommentDTO updated = commentService.update(description, cityId, commentId);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
}
