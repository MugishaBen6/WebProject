package com.flight.management.services;

import com.flight.management.model.FlightAttendant;
import com.flight.management.repositories.FlightAttendantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightAttendantService {

    private final FlightAttendantRepository flightAttendantRepository;

    public FlightAttendantService(FlightAttendantRepository flightAttendantRepository) {
        this.flightAttendantRepository = flightAttendantRepository;
    }

    public List<FlightAttendant> getAllFlightAttendants() {
        return flightAttendantRepository.findAll();
    }

    public Optional<FlightAttendant> getFlightAttendantById(Long staffId) {
        return flightAttendantRepository.findById(staffId);
    }

    public FlightAttendant createFlightAttendant(FlightAttendant flightAttendant) {
        return flightAttendantRepository.save(flightAttendant);
    }

    public FlightAttendant updateFlightAttendant(Long staffId, FlightAttendant updatedFlightAttendant) {
        if (flightAttendantRepository.existsById(staffId)) {
            updatedFlightAttendant.setStaffId(staffId);
            return flightAttendantRepository.save(updatedFlightAttendant);
        }
        return null; // Or throw an exception
    }

    public void deleteFlightAttendant(Long staffId) {
        flightAttendantRepository.deleteById(staffId);
    }

    // Add methods for specific business logic related to FlightAttendants
}