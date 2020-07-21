package com.qualificationtask.flightadvisor.repository;

import com.qualificationtask.flightadvisor.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String role);
}
