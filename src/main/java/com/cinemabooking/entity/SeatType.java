package com.cinemabooking.entity;

public enum SeatType {
    SILVER(1.0),
    GOLD(1.0),
    PLATINUM(1.0);

    private final double multiplier;

    SeatType(double multiplier) {
        this.multiplier = multiplier;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
