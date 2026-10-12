package com.cinemabooking.dto;

import com.cinemabooking.entity.SeatType;
import java.math.BigDecimal;

public class SeatDetailDto {
    private Long id;
    private String rowName;
    private Integer seatNumber;
    private String seatCode;
    private SeatType seatType;
    private BigDecimal price;
    private boolean booked;

    public SeatDetailDto() {}

    public SeatDetailDto(Long id, String rowName, Integer seatNumber, String seatCode, SeatType seatType, BigDecimal price, boolean booked) {
        this.id = id;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatCode = seatCode;
        this.seatType = seatType;
        this.price = price;
        this.booked = booked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public Integer getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(Integer seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatCode() {
        return seatCode;
    }

    public void setSeatCode(String seatCode) {
        this.seatCode = seatCode;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isBooked() {
        return booked;
    }

    public void setBooked(boolean booked) {
        this.booked = booked;
    }
}
