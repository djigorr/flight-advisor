package com.qualificationtask.flightadvisor.dto.mapper;

import com.qualificationtask.flightadvisor.domain.Comment;
import com.qualificationtask.flightadvisor.dto.CommentDTO;

public class CommentMapper {

    public static CommentDTO commentToDto(Comment comment){
        return CommentDTO.builder()
                .description(comment.getDescription())
                .createdDate(comment.getCreatedDate())
                .modifiedDate(comment.getModifiedDate())
                .id(comment.getId())
                .username(comment.getUser().getUsername())
                .build();
    }
}
