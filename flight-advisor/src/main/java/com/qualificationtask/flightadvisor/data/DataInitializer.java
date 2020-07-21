package com.qualificationtask.flightadvisor.data;

import com.qualificationtask.flightadvisor.domain.Role;
import com.qualificationtask.flightadvisor.domain.User;
import com.qualificationtask.flightadvisor.repository.RoleRepository;
import com.qualificationtask.flightadvisor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        Role regular = Role.builder().name("USER").build();
        Role admin = Role.builder().name("ADMIN").build();

        roleRepository.saveAll(Arrays.asList(regular, admin));

        User administrator = User.builder().firstName("Test").lastName("Test").username("Admin").password(passwordEncoder.encode("123")).role(admin).build();
        userRepository.save(administrator);
    }
}
