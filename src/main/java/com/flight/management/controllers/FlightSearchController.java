package com.flight.management.controllers;

import com.flight.management.model.Flight;
import com.flight.management.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/flights/filter")
public class FlightSearchController {
    @Autowired
    private FlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        // If only searchTerm is provided, use existing search
        if ((origin == null || origin.isEmpty()) && (destination == null || destination.isEmpty()) && date == null) {
            Page<Flight> flights = flightService.getAllFlights(pageable);
            return ResponseEntity.ok(flights.getContent());
        }
        // If date is provided, filter by date range
        if (date != null) {
            LocalDateTime start = date.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime end = date.withHour(23).withMinute(59).withSecond(59);
            Page<Flight> flights = flightService.searchFlightsByRoute(
                origin, destination, start, end, pageable);
            return ResponseEntity.ok(flights.getContent());
        }
        // Otherwise, search by origin/destination
        Page<Flight> flights = flightService.searchFlights(
            (origin != null ? origin : "") + " " + (destination != null ? destination : ""), pageable);
        return ResponseEntity.ok(flights.getContent());
    }
} 