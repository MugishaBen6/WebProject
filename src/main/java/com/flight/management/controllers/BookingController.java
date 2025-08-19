package com.flight.management.controllers;

import com.flight.management.model.Booking;
import com.flight.management.model.User;
import com.flight.management.services.BookingService;
import com.flight.management.services.UserService;
import com.flight.management.services.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import com.flight.management.payload.BookingRequest;
import com.flight.management.model.Flight;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserService userService;
    @Autowired
    private FlightService flightService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest bookingRequest) {
        Optional<User> userOpt = userService.getUserById(bookingRequest.getUserId());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found");
        Optional<Flight> flightOpt = flightService.getFlightById(bookingRequest.getFlightId());
        if (flightOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Flight not found");
        Booking booking = new Booking();
        booking.setPrice(bookingRequest.getPrice());
        booking.setUser(userOpt.get());
        booking.setFlight(flightOpt.get());
        Booking saved = bookingService.createBooking(booking);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/my")
    public ResponseEntity<List<Booking>> getMyBookings(Principal principal) {
        // In a real app, get user from principal (JWT). For now, use email from principal name.
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.ok(bookingService.getBookingsByUser(userOpt.get()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id, Principal principal) {
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        if (bookingOpt.isEmpty()) return ResponseEntity.notFound().build();
        Booking booking = bookingOpt.get();
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userOpt.get();
        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.hasRole("ROLE_ADMIN");
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Long id, @RequestBody BookingRequest bookingRequest, Principal principal) {
        Optional<Booking> bookingOpt = bookingService.getBookingById(id);
        if (bookingOpt.isEmpty()) return ResponseEntity.notFound().build();
        Booking booking = bookingOpt.get();
        Optional<User> userOpt = userService.getUserByEmail(principal.getName());
        if (userOpt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        User user = userOpt.get();
        boolean isOwner = booking.getUser().getId().equals(user.getId());
        boolean isAdmin = user.hasRole("ROLE_ADMIN");
        if (!isOwner && !isAdmin) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Update fields
        booking.setPrice(bookingRequest.getPrice());
        Optional<Flight> flightOpt = flightService.getFlightById(bookingRequest.getFlightId());
        if (flightOpt.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Flight not found");
        booking.setFlight(flightOpt.get());
        Booking updated = bookingService.createBooking(booking); // save updated booking
        return ResponseEntity.ok(updated);
    }
} 