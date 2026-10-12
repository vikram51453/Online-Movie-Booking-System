package com.cinemabooking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "seats",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_screen_seat", columnNames = {"screen_id", "row_name", "seat_number"})
    }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @NotBlank
    @Column(name = "row_name", nullable = false, length = 10)
    private String rowName;

    @NotNull
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false, length = 20)
    private SeatType seatType = SeatType.SILVER;

    public Seat() {}

    public Seat(Screen screen, String rowName, Integer seatNumber, SeatType seatType) {
        this.screen = screen;
        this.rowName = rowName;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Screen getScreen() {
        return screen;
    }

    public void setScreen(Screen screen) {
        this.screen = screen;
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

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public String getSeatCode() {
        return rowName + seatNumber;
    }
}
