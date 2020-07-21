package com.qualificationtask.flightadvisor.repository;

import com.qualificationtask.flightadvisor.domain.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
}
