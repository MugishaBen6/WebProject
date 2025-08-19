package com.flight.management.services;

import com.flight.management.model.Flight;
import com.flight.management.model.Airplane;
import com.flight.management.model.Pilot;
import com.flight.management.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightService flightService;

    private Flight testFlight;
    private Airplane testAirplane;
    private List<Pilot> testPilots;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testAirplane = new Airplane("Boeing 737", 180, "Boeing");
        testPilots = Arrays.asList(new Pilot("Captain"), new Pilot("First Officer"));
        
        testFlight = new Flight(
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(2),
            "New York",
            "London",
            testAirplane,
            testPilots,
            null
        );
    }

    @Test
    void whenCreateFlight_thenFlightShouldBeSaved() {
        when(flightRepository.save(any(Flight.class))).thenReturn(testFlight);

        Flight savedFlight = flightService.createFlight(testFlight);

        assertNotNull(savedFlight);
        assertEquals("New York", savedFlight.getOrigin());
        assertEquals("London", savedFlight.getDestination());
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void whenGetAllFlights_thenShouldReturnFlightsList() {
        when(flightRepository.findAll()).thenReturn(Arrays.asList(testFlight));

        List<Flight> flights = flightService.getAllFlights();

        assertNotNull(flights);
        assertEquals(1, flights.size());
        verify(flightRepository, times(1)).findAll();
    }

    @Test
    void whenGetFlightById_thenShouldReturnFlight() {
        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));

        Optional<Flight> found = flightService.getFlightById(1L);

        assertTrue(found.isPresent());
        assertEquals("New York", found.get().getOrigin());
        verify(flightRepository, times(1)).findById(1L);
    }

    @Test
    void whenSearchFlights_thenShouldReturnMatchingFlights() {
        Page<Flight> flightPage = new PageImpl<>(Arrays.asList(testFlight));
        Pageable pageable = PageRequest.of(0, 10);
        
        when(flightRepository.searchFlights(anyString(), any(Pageable.class)))
            .thenReturn(flightPage);

        Page<Flight> result = flightService.searchFlights("New York", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(flightRepository, times(1)).searchFlights(anyString(), any(Pageable.class));
    }

    @Test
    void whenUpdateFlight_thenShouldReturnUpdatedFlight() {
        Flight updatedFlight = testFlight;
        updatedFlight.setDestination("Paris");

        when(flightRepository.findById(1L)).thenReturn(Optional.of(testFlight));
        when(flightRepository.save(any(Flight.class))).thenReturn(updatedFlight);

        Flight result = flightService.updateFlight(1L, updatedFlight);

        assertNotNull(result);
        assertEquals("Paris", result.getDestination());
        verify(flightRepository, times(1)).findById(1L);
        verify(flightRepository, times(1)).save(any(Flight.class));
    }

    @Test
    void whenDeleteFlight_thenShouldDeleteFlight() {
        when(flightRepository.existsById(1L)).thenReturn(true);
        doNothing().when(flightRepository).deleteById(1L);

        flightService.deleteFlight(1L);

        verify(flightRepository, times(1)).deleteById(1L);
    }
} 