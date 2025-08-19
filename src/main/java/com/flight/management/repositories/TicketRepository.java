package com.flight.management.repositories;

import com.flight.management.model.Ticket;
import com.flight.management.model.User;
import com.flight.management.model.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByPassenger(User passenger);

    List<Ticket> findByFlight(Flight flight);

    List<Ticket> findBySeatNumberContainingIgnoreCase(String seatNumber);
    
    @Query("SELECT t FROM Ticket t WHERE " +
           "(:#{#params['seatNumber']} IS NULL OR LOWER(t.seatNumber) LIKE LOWER(CONCAT('%', :#{#params['seatNumber']}, '%'))) AND " +
           "(:#{#params['flightId']} IS NULL OR t.flight.id = CAST(:#{#params['flightId']} AS long))")
    Page<Ticket> findBySearchParams(@Param("params") Map<String, String> searchParams, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.price), 0) FROM Ticket t")
    Double calculateTotalRevenue();

    // Add any custom query methods here if needed
}