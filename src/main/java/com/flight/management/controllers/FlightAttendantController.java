package com.flight.management.controllers;

import com.flight.management.model.FlightAttendant;
import com.flight.management.services.FlightAttendantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/flight-attendants")
public class FlightAttendantController {

    private final FlightAttendantService flightAttendantService;

    public FlightAttendantController(FlightAttendantService flightAttendantService) {
        this.flightAttendantService = flightAttendantService;
    }

    @GetMapping
    public ResponseEntity<List<FlightAttendant>> getAllFlightAttendants() {
        return ResponseEntity.ok(flightAttendantService.getAllFlightAttendants());
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<FlightAttendant> getFlightAttendantById(@PathVariable Long staffId) {
        Optional<FlightAttendant> flightAttendant = flightAttendantService.getFlightAttendantById(staffId);
        return flightAttendant.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<FlightAttendant> createFlightAttendant(@RequestBody FlightAttendant flightAttendant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(flightAttendantService.createFlightAttendant(flightAttendant));
    }

    @PutMapping("/{staffId}")
    public ResponseEntity<FlightAttendant> updateFlightAttendant(@PathVariable Long staffId, @RequestBody FlightAttendant updatedFlightAttendant) {
        FlightAttendant flightAttendant = flightAttendantService.updateFlightAttendant(staffId, updatedFlightAttendant);
        return flightAttendant != null ? ResponseEntity.ok(flightAttendant) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{staffId}")
    public ResponseEntity<Void> deleteFlightAttendant(@PathVariable Long staffId) {
        flightAttendantService.deleteFlightAttendant(staffId);
        return ResponseEntity.noContent().build();
    }
}