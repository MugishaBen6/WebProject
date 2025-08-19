package com.flight.management.repositories;

import com.flight.management.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("SELECT f FROM Flight f WHERE " +
           "LOWER(f.origin) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(f.destination) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Flight> searchFlights(@Param("search") String search, Pageable pageable);

    @Query("SELECT f FROM Flight f WHERE f.origin = :origin " +
           "AND f.destination = :destination " +
           "AND f.departureTime BETWEEN :startTime AND :endTime")
    Page<Flight> findFlightsByRouteAndTimeRange(
        @Param("origin") String origin,
        @Param("destination") String destination,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime,
        Pageable pageable);

    List<Flight> findByOriginContainingIgnoreCaseOrDestinationContainingIgnoreCase(String origin, String destination);
    
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.departureTime <= CURRENT_TIMESTAMP AND f.arrivalTime >= CURRENT_TIMESTAMP")
    Long countActiveFlights();
    
    @Query("SELECT COUNT(f) FROM Flight f WHERE f.departureTime >= CURRENT_TIMESTAMP AND f.departureTime <= :endTime")
    Long countUpcomingFlights(@Param("endTime") LocalDateTime endTime);
    
    @Query("SELECT f FROM Flight f WHERE " +
           "(:#{#params['origin']} IS NULL OR LOWER(f.origin) LIKE LOWER(CONCAT('%', :#{#params['origin']}, '%'))) AND " +
           "(:#{#params['destination']} IS NULL OR LOWER(f.destination) LIKE LOWER(CONCAT('%', :#{#params['destination']}, '%')))")
    Page<Flight> findBySearchParams(@Param("params") Map<String, String> searchParams, Pageable pageable);
}