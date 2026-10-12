package com.cinemabooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(
    name = "booking_seats",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_show_seat_reservation", columnNames = {"show_id", "seat_id"})
    },
    indexes = {
        @Index(name = "idx_bseat_show", columnList = "show_id"),
        @Index(name = "idx_bseat_booking", columnList = "booking_id")
    }
)
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "seat_number", nullable = false, length = 15)
    private String seatNumber;

    @NotNull
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    public BookingSeat() {}

    public BookingSeat(Booking booking, Show show, Seat seat, String seatNumber, BigDecimal price) {
        this.booking = booking;
        this.show = show;
        this.seat = seat;
        this.seatNumber = seatNumber;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Show getShow() {
        return show;
    }

    public void setShow(Show show) {
        this.show = show;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
