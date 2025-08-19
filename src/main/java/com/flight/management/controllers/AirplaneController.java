package com.flight.management.controllers;

import com.flight.management.model.Airplane;
import com.flight.management.services.AirplaneService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/airplanes")
public class AirplaneController {

    private final AirplaneService airplaneService;

    public AirplaneController(AirplaneService airplaneService) {
        this.airplaneService = airplaneService;
    }

    @GetMapping
    public ResponseEntity<List<Airplane>> getAllAirplanes() {
        return ResponseEntity.ok(airplaneService.getAllAirplanes());
    }

    @GetMapping("/{airplaneId}")
    public ResponseEntity<Airplane> getAirplaneById(@PathVariable Long airplaneId) {
        Optional<Airplane> airplane = airplaneService.getAirplaneById(airplaneId);
        return airplane.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Airplane> createAirplane(@RequestBody Airplane airplane) {
        return ResponseEntity.status(HttpStatus.CREATED).body(airplaneService.createAirplane(airplane));
    }

    @PutMapping("/{airplaneId}")
    public ResponseEntity<Airplane> updateAirplane(@PathVariable Long airplaneId, @RequestBody Airplane updatedAirplane) {
        Airplane airplane = airplaneService.updateAirplane(airplaneId, updatedAirplane);
        return airplane != null ? ResponseEntity.ok(airplane) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{airplaneId}")
    public ResponseEntity<Void> deleteAirplane(@PathVariable Long airplaneId) {
        airplaneService.deleteAirplane(airplaneId);
        return ResponseEntity.noContent().build();
    }
}