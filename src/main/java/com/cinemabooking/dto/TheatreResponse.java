package com.cinemabooking.dto;

import com.cinemabooking.entity.Theatre;

public class TheatreResponse {
    private Long id;
    private String name;
    private String city;
    private String address;
    private Integer totalScreens;
    private boolean active;

    public TheatreResponse() {}

    public static TheatreResponse fromEntity(Theatre theatre) {
        if (theatre == null) return null;
        TheatreResponse dto = new TheatreResponse();
        dto.setId(theatre.getId());
        dto.setName(theatre.getName());
        dto.setCity(theatre.getCity());
        dto.setAddress(theatre.getAddress());
        dto.setTotalScreens(theatre.getTotalScreens());
        dto.setActive(theatre.isActive());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
