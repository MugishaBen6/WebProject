package com.flight.management.services;

import com.flight.management.model.Booking;
import com.flight.management.model.User;
import com.flight.management.model.Flight;
import com.flight.management.repositories.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Service
public class BookingService {
    @Autowired
    private BookingRepository bookingRepository;

    public Booking createBooking(Booking booking) {
        // Prevent double booking for the same user/flight
        List<Booking> userBookings = bookingRepository.findByUser(booking.getUser());
        boolean alreadyBooked = userBookings.stream().anyMatch(b -> b.getFlight().getFlightId().equals(booking.getFlight().getFlightId()));
        if (alreadyBooked) {
            throw new RuntimeException("User already booked this flight");
        }
        // Prevent booking if flight is full
        Flight flight = booking.getFlight();
        long bookingCount = bookingRepository.findByUser(booking.getUser()).stream().filter(b -> b.getFlight().getFlightId().equals(flight.getFlightId())).count();
        long totalBookings = StreamSupport.stream(bookingRepository.findAll().spliterator(), false)
            .filter(b -> b.getFlight().getFlightId().equals(flight.getFlightId())).count();
        int capacity = flight.getAirplane().getCapacity();
        if (totalBookings >= capacity) {
            throw new RuntimeException("Flight is full");
        }
        return bookingRepository.save(booking);
    }

    public List<Booking> getAllBookings() {
        return (List<Booking>) bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByUser(User user) {
        // Assuming BookingRepository has a method for this, otherwise implement custom
        return bookingRepository.findByUser(user);
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }
} 