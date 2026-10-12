package com.cinemabooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theatres")
public class Theatre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Theatre name is required")
    @Size(max = 120)
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @NotBlank(message = "City is required")
    @Size(max = 80)
    @Column(name = "city", nullable = false, length = 80)
    private String city;

    @NotBlank(message = "Address is required")
    @Size(max = 255)
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @Column(name = "total_screens")
    private Integer totalScreens = 1;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @JsonIgnore
    @OneToMany(mappedBy = "theatre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Screen> screens = new ArrayList<>();

    public Theatre() {}

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

    public List<Screen> getScreens() {
        return screens;
    }

    public void setScreens(List<Screen> screens) {
        this.screens = screens;
    }
}
