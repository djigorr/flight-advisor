package com.qualificationtask.flightadvisor.service;

import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Comment;
import com.qualificationtask.flightadvisor.dto.CommentDTO;
import com.qualificationtask.flightadvisor.dto.mapper.CommentMapper;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.CommentRepository;
import com.qualificationtask.flightadvisor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@Service
public class CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    public CommentDTO create(String description, Long cityId){
        City city = cityRepository.findById(cityId).<RuntimeException>orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found.");
        });
        Comment comment = Comment.builder()
                .description(description)
                .city(city)
                .user(userService.getCurrentUser()).build();
        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.commentToDto(savedComment);
    }

    public CommentDTO update(String description, Long cityId, Long commentId){
        checkCity(cityId);
        Comment comment = checkComment(commentId);
        if(!comment.getUser().equals(userService.getCurrentUser())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Comment is not posted by logged user.");
        }
        comment.setDescription(description);
        Comment updatedComment = commentRepository.save(comment);
        return CommentMapper.commentToDto(updatedComment);
    }

    public void delete(Long cityId, Long commentId){
        checkCity(cityId);
        Comment comment = checkComment(commentId);
        if(!comment.getUser().equals(userService.getCurrentUser())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Comment is not posted by logged user.");
        }
        commentRepository.delete(comment);
    }

    private City checkCity(Long cityId){
        return cityRepository.findById(cityId).<RuntimeException>orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "City not found.");
        });
    }

    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId).<RuntimeException>orElseThrow(() -> {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found.");
        });
    }
}
