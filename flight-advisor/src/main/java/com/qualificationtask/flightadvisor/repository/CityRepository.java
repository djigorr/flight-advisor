package com.qualificationtask.flightadvisor.repository;

import com.qualificationtask.flightadvisor.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByNameEqualsIgnoringCase(String name);
}
