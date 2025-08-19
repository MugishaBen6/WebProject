package com.flight.management.services;

import com.flight.management.model.Airplane;
import com.flight.management.repositories.AirplaneRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AirplaneService {

    private final AirplaneRepository airplaneRepository;

    public AirplaneService(AirplaneRepository airplaneRepository) {
        this.airplaneRepository = airplaneRepository;
    }

    public List<Airplane> getAllAirplanes() {
        return airplaneRepository.findAll();
    }

    public Optional<Airplane> getAirplaneById(Long airplaneId) {
        return airplaneRepository.findById(airplaneId);
    }

    public Airplane createAirplane(Airplane airplane) {
        return airplaneRepository.save(airplane);
    }

    public Airplane updateAirplane(Long airplaneId, Airplane updatedAirplane) {
        if (airplaneRepository.existsById(airplaneId)) {
            updatedAirplane.setAirplaneId(airplaneId);
            return airplaneRepository.save(updatedAirplane);
        }
        return null; // Or throw an exception
    }

    public void deleteAirplane(Long airplaneId) {
        airplaneRepository.deleteById(airplaneId);
    }

    // Add methods for specific business logic related to Airplanes
}