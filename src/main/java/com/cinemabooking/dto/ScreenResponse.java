package com.cinemabooking.dto;

import com.cinemabooking.entity.Screen;

public class ScreenResponse {
    private Long id;
    private Long theatreId;
    private String theatreName;
    private String name;
    private String screenType;
    private Integer totalSeats;
    private Integer rowsCount;
    private Integer colsCount;

    public ScreenResponse() {}

    public static ScreenResponse fromEntity(Screen screen) {
        if (screen == null) return null;
        ScreenResponse dto = new ScreenResponse();
        dto.setId(screen.getId());
        if (screen.getTheatre() != null) {
            dto.setTheatreId(screen.getTheatre().getId());
            dto.setTheatreName(screen.getTheatre().getName());
        }
        dto.setName(screen.getName());
        dto.setScreenType(screen.getScreenType());
        dto.setTotalSeats(screen.getTotalSeats());
        dto.setRowsCount(screen.getRowsCount());
        dto.setColsCount(screen.getColsCount());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(Long theatreId) {
        this.theatreId = theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
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
}
