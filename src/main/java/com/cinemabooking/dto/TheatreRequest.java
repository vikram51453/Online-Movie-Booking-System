package com.cinemabooking.dto;

import com.cinemabooking.entity.Theatre;
import jakarta.validation.constraints.NotBlank;

public class TheatreRequest {

    @NotBlank(message = "Theatre name is required")
    private String name;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Address is required")
    private String address;

    private Integer totalScreens = 1;
    private boolean active = true;

    public TheatreRequest() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getTotalScreens() {
        return totalScreens;
    }

    public void setTotalScreens(Integer totalScreens) {
        this.totalScreens = totalScreens;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
