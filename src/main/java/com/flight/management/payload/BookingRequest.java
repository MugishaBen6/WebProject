package com.flight.management.payload;

import java.math.BigDecimal;

public class BookingRequest {
    private BigDecimal price;
    private Long userId;
    private Long flightId;

    public BookingRequest() {}

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
} 