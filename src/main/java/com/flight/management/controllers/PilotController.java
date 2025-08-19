package com.flight.management.controllers;

import com.flight.management.model.Pilot;
import com.flight.management.services.PilotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pilots")
public class PilotController {

    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @GetMapping
    public ResponseEntity<List<Pilot>> getAllPilots() {
        return ResponseEntity.ok(pilotService.getAllPilots());
    }

    @GetMapping("/{licenseNumber}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long licenseNumber) {
        Optional<Pilot> pilot = pilotService.getPilotById(licenseNumber);
        return pilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilot));
    }

    @PutMapping("/{licenseNumber}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long licenseNumber, @RequestBody Pilot updatedPilot) {
        Pilot pilot = pilotService.updatePilot(licenseNumber, updatedPilot);
        return pilot != null ? ResponseEntity.ok(pilot) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{licenseNumber}")
    public ResponseEntity<Void> deletePilot(@PathVariable Long licenseNumber) {
        pilotService.deletePilot(licenseNumber);
        return ResponseEntity.noContent().build();
    }
}