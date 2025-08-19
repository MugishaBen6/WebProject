package com.flight.management.services;

import com.flight.management.model.Pilot;
import com.flight.management.repositories.PilotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PilotService {

    private final PilotRepository pilotRepository;

    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    public List<Pilot> getAllPilots() {
        return pilotRepository.findAll();
    }

    public Optional<Pilot> getPilotById(Long licenseNumber) {
        return pilotRepository.findById(licenseNumber);
    }

    public Pilot createPilot(Pilot pilot) {
        return pilotRepository.save(pilot);
    }

    public Pilot updatePilot(Long licenseNumber, Pilot updatedPilot) {
        if (pilotRepository.existsById(licenseNumber)) {
            updatedPilot.setLicenseNumber(licenseNumber);
            return pilotRepository.save(updatedPilot);
        }
        return null; // Or throw an exception
    }

    public void deletePilot(Long licenseNumber) {
        pilotRepository.deleteById(licenseNumber);
    }

    // Add methods for specific business logic related to Pilots
}