package com.flight.management.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.math.BigDecimal;
import com.flight.management.model.Booking;
import com.flight.management.model.User;
import java.util.List;

public interface BookingRepository extends CrudRepository<Booking, Long> {

    @Query("SELECT SUM(b.price) FROM Booking b")
    BigDecimal sumTotalRevenue();

    List<Booking> findByUser(User user);
} 