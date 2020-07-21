package com.qualificationtask.flightadvisor.dto.mapper;

import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.dto.UserDTO;

public class UserMapper {

    public static UserDTO userToDto(User user){
        return UserDTO.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .role(user.getRole().getName())
                .build();
    }

    public static User dtoToUser(UserDTO dto){
        return User.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(dto.getUsername())
                .build();
    }
}
