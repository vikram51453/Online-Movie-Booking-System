package com.cinemabooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BookingRequest {

    @NotNull(message = "Show ID is required")
    private Long showId;

    @NotEmpty(message = "Please select at least one seat")
    private List<Long> seatIds;

    private String paymentMethod = "Simulated Card / UPI";

    public BookingRequest() {}

    public BookingRequest(Long showId, List<Long> seatIds) {
        this.showId = showId;
        this.seatIds = seatIds;
    }

    public Long getShowId() {
        return showId;
    }

    public void setShowId(Long showId) {
        this.showId = showId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
