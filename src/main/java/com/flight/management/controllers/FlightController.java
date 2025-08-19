package com.flight.management.controllers;

import com.flight.management.model.Flight;
import com.flight.management.services.FlightService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        return ResponseEntity.ok(flightService.getAllFlights());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<Flight>> getAllFlightsPaged(Pageable pageable) {
        return ResponseEntity.ok(flightService.getAllFlights(pageable));
    }

    @GetMapping("/{flightId}")
    public ResponseEntity<Flight> getFlightById(@PathVariable Long flightId) {
        Optional<Flight> flight = flightService.getFlightById(flightId);
        return flight.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> createFlight(@RequestBody Flight flight) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightService.createFlight(flight));
    }

    @PutMapping("/{flightId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> updateFlight(@PathVariable Long flightId, @RequestBody Flight updatedFlight) {
        Flight flight = flightService.updateFlight(flightId, updatedFlight);
        return ResponseEntity.ok(flight);
    }

    @DeleteMapping("/{flightId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFlight(@PathVariable Long flightId) {
        flightService.deleteFlight(flightId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Flight>> searchFlights(
            @RequestParam(required = false) String searchTerm,
            Pageable pageable) {
        return ResponseEntity.ok(flightService.searchFlights(searchTerm, pageable));
    }

    @GetMapping("/route")
    public ResponseEntity<Page<Flight>> searchFlightsByRoute(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            Pageable pageable) {
        return ResponseEntity.ok(flightService.searchFlightsByRoute(
            origin, destination, startTime, endTime, pageable));
    }
}