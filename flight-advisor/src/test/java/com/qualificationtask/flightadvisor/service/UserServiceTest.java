package com.qualificationtask.flightadvisor.service;

import static org.junit.jupiter.api.Assertions.*;
import com.qualificationtask.flightadvisor.domain.Role;
import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.dto.LoginDTO;
import com.qualificationtask.flightadvisor.dto.UserDTO;
import com.qualificationtask.flightadvisor.repository.RoleRepository;
import com.qualificationtask.flightadvisor.repository.UserRepository;
import com.qualificationtask.flightadvisor.security.TokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    TokenUtils tokenUtils;

    @InjectMocks
    UserService userService;

    @Test
    public void testRegisterUserSuccessful() {
        UserDTO dto = UserDTO.builder().firstName("Igor").lastName("Djukic").password("abc").username("igi").build();
        Role role = Role.builder().id(2L).name("USER").build();
        User user = User.builder().firstName("Igor").lastName("Djukic").password("abc").username("igi").role(role).build();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(roleRepository.findByName(anyString())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("abc");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO registered = userService.register(dto);

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(roleRepository, times(1)).findByName(anyString());

        assertEquals(registered.getFirstName(),dto.getFirstName());
        assertEquals(registered.getLastName(),dto.getLastName());
        assertEquals(registered.getUsername(),dto.getUsername());
        assertEquals(registered.getRole(),role.getName());
    }

    @Test
    public void testRegisterUserAlreadyExistFail() {
        UserDTO dto = UserDTO.builder().firstName("Igor").lastName("Djukic").password("abc").username("igi").build();
        Role role = Role.builder().id(2L).name("USER").build();
        User user = User.builder().firstName("Igor").lastName("Djukic").password("abc").username("igi").role(role).build();
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> userService.register(dto));

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(userRepository, times(0)).save(any(User.class));
        verify(passwordEncoder, times(0)).encode(anyString());
        verify(roleRepository, times(0)).findByName(anyString());

        assertEquals(exception.getStatus(),HttpStatus.BAD_REQUEST);
        assertEquals(exception.getReason(),"User with username igi already exists.");
    }

    @Test
    public void testLoginUserSuccessful(){
        LoginDTO login = new LoginDTO("Admin", "123");
        Role role = Role.builder().id(1L).name("ADMIN").build();
        User user = User.builder().firstName("admin").lastName("admin").password("123").username("Admin").role(role).build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(tokenUtils.generateToken(any(UserDetails.class))).thenReturn("12345");
        String token = userService.login(login);

        verify(userRepository,times(1)).findByUsername(anyString());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenUtils, times(1)).generateToken(any(UserDetails.class));

        assertEquals(token, "12345");
    }

    @Test
    public void testLoginUserNotExistingFail(){
        LoginDTO login = new LoginDTO("Admin", "123");
        Role role = Role.builder().id(1L).name("ADMIN").build();
        User user = User.builder().firstName("admin").lastName("admin").password("123").username("Admin").role(role).build();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.login(login));

        verify(userRepository,times(1)).findByUsername(anyString());
        verify(authenticationManager, times(1)).authenticate(any());
        verify(tokenUtils, times(0)).generateToken(any(UserDetails.class));
    }
}