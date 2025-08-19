package com.flight.management.services;

import com.flight.management.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PilotRepository pilotRepository;

    @Autowired
    private AirplaneRepository airplaneRepository;

    @Autowired
    private BookingRepository bookingRepository;

    public Map<String, Object> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Total counts
        summary.put("totalFlights", flightRepository.count());
        summary.put("totalTickets", ticketRepository.count());
        summary.put("totalUsers", userRepository.count());
        summary.put("totalPilots", pilotRepository.count());
        summary.put("totalAirplanes", airplaneRepository.count());
        
        // Revenue summary (using optimized query)
        summary.put("totalRevenue", ticketRepository.calculateTotalRevenue());
        
        // Active flights
        summary.put("activeFlights", flightRepository.countActiveFlights());
        
        // Upcoming flights in next 24 hours
        LocalDateTime endTime = LocalDateTime.now().plusHours(24);
        summary.put("upcomingFlights", flightRepository.countUpcomingFlights(endTime));
        
        return summary;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalFlights", flightRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("totalUsers", userRepository.count());
        // Assuming Booking has a field 'price' of type BigDecimal or double
        BigDecimal totalRevenue = bookingRepository.sumTotalRevenue();
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        return stats;
    }
} 