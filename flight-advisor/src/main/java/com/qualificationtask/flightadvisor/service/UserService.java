package com.qualificationtask.flightadvisor.service;

import com.qualificationtask.flightadvisor.domain.Role;
import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.dto.LoginDTO;
import com.qualificationtask.flightadvisor.dto.UserDTO;
import com.qualificationtask.flightadvisor.dto.mapper.UserMapper;
import com.qualificationtask.flightadvisor.repository.RoleRepository;
import com.qualificationtask.flightadvisor.repository.UserRepository;
import com.qualificationtask.flightadvisor.security.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Optional;

@Transactional
@Service
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenUtils tokenUtils;



    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("Invalid credentials"));
        return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())));
    }

    public String login(LoginDTO dto){
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
        authenticationManager.authenticate(token);
        UserDetails details = loadUserByUsername(dto.getUsername());
        return tokenUtils.generateToken(details);
    }

    public UserDTO register(UserDTO dto) {
        Optional<User> optional = userRepository.findByUsername(dto.getUsername());
        if (optional.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with username " + dto.getUsername() + " already exists.");
        }
        Role role = roleRepository.findByName("USER");
        User user = UserMapper.dtoToUser(dto);
        user.setRole(role);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User created = userRepository.save(user);
        return UserMapper.userToDto(created);
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)authentication.getPrincipal();
        return userRepository.findByUsername(user.getUsername()).orElse(null);
    }
}
