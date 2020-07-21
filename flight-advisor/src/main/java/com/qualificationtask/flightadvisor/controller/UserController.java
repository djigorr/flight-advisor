package com.qualificationtask.flightadvisor.controller;

import com.qualificationtask.flightadvisor.dto.LoginDTO;
import com.qualificationtask.flightadvisor.dto.UserDTO;
import com.qualificationtask.flightadvisor.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api("Controller for exposing User related operations via REST endpoint.")
public class UserController {

    @Autowired
    UserService userService;

    @ApiOperation("Logins user.")
    @PostMapping("/api/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

    @ApiOperation("Registers user.")
    @PostMapping("/api/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO dto) {
        UserDTO created = userService.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
