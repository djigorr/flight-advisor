package com.qualificationtask.flightadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long id;

    @NotEmpty(message = "Description is required")
    private String description;

    private LocalDateTime createdDate;

    private LocalDateTime modifiedDate;

    private String username;
}
