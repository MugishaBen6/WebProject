package com.flight.management.services;

import com.flight.management.model.Flight;
import com.flight.management.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Page<Flight> getAllFlights(Pageable pageable) {
        return flightRepository.findAll(pageable);
    }

    public Optional<Flight> getFlightById(Long id) {
        return flightRepository.findById(id);
    }

    public Page<Flight> searchFlights(String searchTerm, Pageable pageable) {
        return flightRepository.searchFlights(searchTerm, pageable);
    }

    @Transactional
    public Flight createFlight(Flight flight) {
        validateFlight(flight);
        return flightRepository.save(flight);
    }

    @Transactional
    public Flight updateFlight(Long id, Flight updatedFlight) {
        return flightRepository.findById(id)
                .map(flight -> {
                    validateFlight(updatedFlight);
                    flight.setOrigin(updatedFlight.getOrigin());
                    flight.setDestination(updatedFlight.getDestination());
                    flight.setDepartureTime(updatedFlight.getDepartureTime());
                    flight.setArrivalTime(updatedFlight.getArrivalTime());
                    flight.setAirplane(updatedFlight.getAirplane());
                    flight.setPilots(updatedFlight.getPilots());
                    flight.setAttendants(updatedFlight.getAttendants());
                    return flightRepository.save(flight);
                })
                .orElseThrow(() -> new RuntimeException("Flight not found"));
    }

    @Transactional
    public void deleteFlight(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new RuntimeException("Flight not found");
        }
        flightRepository.deleteById(id);
    }

    public Page<Flight> searchFlightsByRoute(
            String origin, 
            String destination, 
            LocalDateTime startTime,
            LocalDateTime endTime,
            Pageable pageable) {
        return flightRepository.findFlightsByRouteAndTimeRange(
            origin, destination, startTime, endTime, pageable);
    }

    private void validateFlight(Flight flight) {
        if (flight.getDepartureTime().isAfter(flight.getArrivalTime())) {
            throw new RuntimeException("Departure time cannot be after arrival time");
        }
        if (flight.getAirplane() == null) {
            throw new RuntimeException("Flight must have an assigned airplane");
        }
        if (flight.getPilots() == null || flight.getPilots().isEmpty()) {
            throw new RuntimeException("Flight must have at least one pilot assigned");
        }
    }
}