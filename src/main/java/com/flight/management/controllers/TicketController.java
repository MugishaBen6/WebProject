package com.flight.management.controllers;

import com.flight.management.model.Ticket;
import com.flight.management.model.User;
import com.flight.management.model.Flight;
import com.flight.management.services.TicketService;
import com.flight.management.services.UserService;
import com.flight.management.services.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final FlightService flightService;


    public TicketController(TicketService ticketService, UserService userService, FlightService flightService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.flightService = flightService;
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<Ticket> getTicketById(@PathVariable Long ticketId) {
        Optional<Ticket> ticket = ticketService.getTicketById(ticketId);
        return ticket.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Ticket> createTicket(@RequestBody Ticket ticket) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(ticket));
    }

    @PutMapping("/{ticketId}")
    public ResponseEntity<Ticket> updateTicket(@PathVariable Long ticketId, @RequestBody Ticket updatedTicket) {
        Ticket ticket = ticketService.updateTicket(ticketId, updatedTicket);
        return ticket != null ? ResponseEntity.ok(ticket) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{ticketId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long ticketId) {
        ticketService.deleteTicket(ticketId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<Ticket>> getTicketsByPassenger(@PathVariable Long passengerId) {
        Optional<User> passenger = userService.getUserById(passengerId);
        if (passenger.isPresent()) {
            return ResponseEntity.ok(ticketService.getTicketsByPassenger(passenger.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/flight/{flightId}")
    public ResponseEntity<List<Ticket>> getTicketsByFlight(@PathVariable Long flightId) {
        Optional<Flight> flight = flightService.getFlightById(flightId);
        if (flight.isPresent()) {
            return ResponseEntity.ok(ticketService.getTicketsByFlight(flight.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Ticket>> getMyTickets(@RequestParam Long userId) {
        // In a real app, get userId from the authenticated principal
        User user = userService.getUserById(userId).orElse(null);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(ticketService.getTicketsByPassenger(user));
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<Ticket>> getAllTicketsPaged(Pageable pageable) {
        return ResponseEntity.ok(ticketService.getAllTickets(pageable));
    }
}