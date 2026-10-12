package com.cinemabooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screens")
public class Screen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "theatre_id", nullable = false)
    private Theatre theatre;

    @NotBlank(message = "Screen name is required")
    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @Column(name = "screen_type", length = 50)
    private String screenType = "Standard 2D";

    @NotNull(message = "Total seats is required")
    @Column(name = "total_seats", nullable = false)
    private Integer totalSeats;

    @Column(name = "rows_count", nullable = false)
    private Integer rowsCount = 8;

    @Column(name = "cols_count", nullable = false)
    private Integer colsCount = 10;

    @JsonIgnore
    @OneToMany(mappedBy = "screen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public Screen() {}

    public Screen(Theatre theatre, String name, String screenType, Integer totalSeats, Integer rowsCount, Integer colsCount) {
        this.theatre = theatre;
        this.name = name;
        this.screenType = screenType;
        this.totalSeats = totalSeats;
        this.rowsCount = rowsCount;
        this.colsCount = colsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Theatre getTheatre() {
        return theatre;
    }

    public void setTheatre(Theatre theatre) {
        this.theatre = theatre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public Integer getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(Integer totalSeats) {
        this.totalSeats = totalSeats;
    }

    public Integer getRowsCount() {
        return rowsCount;
    }

    public void setRowsCount(Integer rowsCount) {
        this.rowsCount = rowsCount;
    }

    public Integer getColsCount() {
        return colsCount;
    }

    public void setColsCount(Integer colsCount) {
        this.colsCount = colsCount;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }
}
