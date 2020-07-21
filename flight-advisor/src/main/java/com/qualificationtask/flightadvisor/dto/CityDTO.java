package com.qualificationtask.flightadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDTO {

    private Long id;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Country is required")
    private String country;

    @NotEmpty(message = "Description is required")
    private String description;

    private List<CommentDTO> comments = new ArrayList<>() ;
}
