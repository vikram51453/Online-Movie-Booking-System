package com.cinemabooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ScreenRequest {

    @NotNull(message = "Theatre ID is required")
    private Long theatreId;

    @NotBlank(message = "Screen name is required")
    private String name;

    private String screenType = "Standard 2D";

    @NotNull(message = "Rows count is required")
    private Integer rowsCount = 8;

    @NotNull(message = "Cols count is required")
    private Integer colsCount = 10;

    public ScreenRequest() {}

    public Long getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(Long theatreId) {
        this.theatreId = theatreId;
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
