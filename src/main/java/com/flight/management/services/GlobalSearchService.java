package com.flight.management.services;

import com.flight.management.repositories.*;
import com.flight.management.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GlobalSearchService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private PilotRepository pilotRepository;

    @Autowired
    private AirplaneRepository airplaneRepository;

    public Map<String, Object> globalSearch(String query) {
        Map<String, Object> results = new HashMap<>();
        
        // Search flights by origin, destination
        List<Flight> flights = flightRepository.findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(
            query, query);
        results.put("flights", flights);

        // Search users by email or full name
        List<User> users = userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(
            query, query);
        results.put("users", users.stream()
            .map(user -> Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "fullName", user.getFullName()
            ))
            .collect(Collectors.toList()));

        // Search tickets by seat number
        List<Ticket> tickets = ticketRepository.findBySeatNumberContainingIgnoreCase(query);
        results.put("tickets", tickets);

        // Search pilots by rank
        List<Pilot> pilots = pilotRepository.findByRankContainingIgnoreCase(query);
        results.put("pilots", pilots);

        // Search airplanes by model or manufacturer
        List<Airplane> airplanes = airplaneRepository.findByModelContainingIgnoreCaseOrManufacturerContainingIgnoreCase(
            query, query);
        results.put("airplanes", airplanes);

        return results;
    }
} 