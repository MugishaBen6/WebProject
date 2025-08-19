package com.flight.management.controllers;

import com.flight.management.repositories.*;
import com.flight.management.services.GlobalSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Autowired
    private GlobalSearchService globalSearchService;
    
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

    @GetMapping("/global")
    public ResponseEntity<?> globalSearch(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(globalSearchService.globalSearch(query));
    }

    @GetMapping("/table/{entity}")
    public ResponseEntity<?> tableSearch(
            @PathVariable String entity,
            @RequestParam(required = false) Map<String, String> searchParams,
            Pageable pageable) {
        
        // Convert the entity string to determine which repository to use
        switch (entity.toLowerCase()) {
            case "flights":
                return ResponseEntity.ok(flightRepository.findBySearchParams(searchParams, pageable));
            case "users":
                return ResponseEntity.ok(userRepository.findBySearchParams(searchParams, pageable));
            case "tickets":
                return ResponseEntity.ok(ticketRepository.findBySearchParams(searchParams, pageable));
            case "pilots":
                return ResponseEntity.ok(pilotRepository.findBySearchParams(searchParams, pageable));
            case "airplanes":
                return ResponseEntity.ok(airplaneRepository.findBySearchParams(searchParams, pageable));
            default:
                return ResponseEntity.badRequest().body("Invalid entity type");
        }
    }
} 