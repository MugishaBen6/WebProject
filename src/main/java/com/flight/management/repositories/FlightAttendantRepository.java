package com.flight.management.repositories;

import com.flight.management.model.FlightAttendant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightAttendantRepository extends JpaRepository<FlightAttendant, Long> {

    // Add any custom query methods here if needed
}