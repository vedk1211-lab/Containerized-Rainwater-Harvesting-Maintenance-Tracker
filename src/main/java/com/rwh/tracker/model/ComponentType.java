package com.rwh.tracker.model;

/**
 * Enum representing the type of a rainwater harvesting component.
 * Each type carries a default maintenance interval (in days).
 */
public enum ComponentType {
    TANK(180),
    FILTER(30),
    FIRST_FLUSH_DIVERTER(30),
    GUTTER_MESH(90),
    PUMP(180),
    OVERFLOW(90);

    private final int defaultIntervalDays;

    ComponentType(int defaultIntervalDays) {
        this.defaultIntervalDays = defaultIntervalDays;
    }

    public int getDefaultIntervalDays() {
        return defaultIntervalDays;
    }
}
