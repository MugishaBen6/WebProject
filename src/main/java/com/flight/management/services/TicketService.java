package com.flight.management.services;

import com.flight.management.model.Ticket;
import com.flight.management.model.User;
import com.flight.management.model.Flight;
import com.flight.management.repositories.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Optional<Ticket> getTicketById(Long ticketId) {
        return ticketRepository.findById(ticketId);
    }

    public Ticket createTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Long ticketId, Ticket updatedTicket) {
        if (ticketRepository.existsById(ticketId)) {
            updatedTicket.setTicketId(ticketId);
            return ticketRepository.save(updatedTicket);
        }
        return null; // Or throw an exception
    }

    public void deleteTicket(Long ticketId) {
        ticketRepository.deleteById(ticketId);
    }

    public List<Ticket> getTicketsByPassenger(User passenger) {
        return ticketRepository.findByPassenger(passenger);
    }

    public List<Ticket> getTicketsByFlight(Flight flight) {
        return ticketRepository.findByFlight(flight);
    }

    public Page<Ticket> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable);
    }

    // Add methods for specific business logic related to Tickets
}