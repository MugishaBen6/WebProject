package com.flight.management.controllers;

import com.flight.management.repositories.TicketRepository;
import com.flight.management.repositories.UserRepository;
import com.flight.management.repositories.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FlightRepository flightRepository;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSummaryReport() {
        Map<String, Object> report = new HashMap<>();
        report.put("totalFlights", flightRepository.count());
        report.put("totalBookings", ticketRepository.count());
        report.put("totalUsers", userRepository.count());
        report.put("totalRevenue", ticketRepository.calculateTotalRevenue());
        return ResponseEntity.ok(report);
    }
} 