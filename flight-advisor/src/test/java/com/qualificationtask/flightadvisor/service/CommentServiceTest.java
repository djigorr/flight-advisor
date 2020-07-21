package com.qualificationtask.flightadvisor.service;

import static org.junit.jupiter.api.Assertions.*;

import com.qualificationtask.flightadvisor.domain.City;
import com.qualificationtask.flightadvisor.domain.Comment;
import com.qualificationtask.flightadvisor.domain.Role;
import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.dto.CommentDTO;
import com.qualificationtask.flightadvisor.repository.CityRepository;
import com.qualificationtask.flightadvisor.repository.CommentRepository;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    CityRepository cityRepository;

    @Mock
    UserService userService;

    @InjectMocks
    CommentService commentService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateSuccessful() {
        User user = User.builder().username("igi").password("abc").firstName("Igor").lastName("Djukic").role(new Role()).build();
        City city = City.builder().id(1L).name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        String description = "test comment";
        Comment comment = Comment.builder().description(description).user(user).createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).city(city).id(1L).build();

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(userService.getCurrentUser()).thenReturn(user);
        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));

        CommentDTO saved = commentService.create(description, city.getId());

        verify(userService, times(1)).getCurrentUser();
        verify(cityRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));

        assertEquals(saved.getDescription(),description);
        assertEquals(saved.getUsername(), user.getUsername());
    }

    @Test
    public void testCreateCommentInvalidCityFail() {
        when(cityRepository.findById(any())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.create("some comment", 5L));

        verify(userService, times(0)).getCurrentUser();
        verify(cityRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(0)).save(any(Comment.class));

        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
        assertEquals(exception.getReason(),"City not found.");
    }

    @Test
    public void testDeleteCommentNotFoundFail() {
        City city = City.builder().id(1L).name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();

        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> commentService.delete(10L, 10L));

        verify(cityRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(0)).delete(any(Comment.class));
        verify(userService, times(0)).getCurrentUser();

        assertEquals(exception.getStatus(),HttpStatus.NOT_FOUND);
        assertEquals(exception.getReason(),"Comment not found.");
    }

    @Test
    public void testDeleteSuccessful() {
        User user = User.builder().username("igi").password("abc").firstName("Igor").lastName("Djukic").role(new Role()).build();
        City city = City.builder().id(1L).name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        Comment comment = Comment.builder().description("test comment").user(user).createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).city(city).id(1L).build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.delete(1L, 1L);

        verify(cityRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).delete(any(Comment.class));
        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    public void testUpdateSuccessful() {
        User user = User.builder().username("igi").password("abc").firstName("Igor").lastName("Djukic").role(new Role()).build();
        City city = City.builder().id(1L).name("Novi Sad").country("Serbia").description("Nice city").comments(new ArrayList<>()).build();
        Comment comment = Comment.builder().description("original comment").user(user).createdDate(LocalDateTime.now()).modifiedDate(LocalDateTime.now()).city(city).id(1L).build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(cityRepository.findById(anyLong())).thenReturn(Optional.of(city));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO updated = commentService.update("updated comment", 1L, 1L);

        verify(commentRepository, times(1)).findById(anyLong());
        verify(cityRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
        verify(userService, times(1)).getCurrentUser();

        assertEquals(updated.getDescription(), "updated comment");
        assertEquals(updated.getUsername(),user.getUsername());
    }
}