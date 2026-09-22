package com.rwh.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Represents an individual component (tank, filter, pump, etc.)
 * belonging to a rainwater harvesting system.
 */
@Entity
@Table(name = "components")
public class Component {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "system_id", nullable = false)
    private RwhSystem system;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Component type is required")
    private ComponentType type;

    @NotBlank(message = "Component name is required")
    @Column(nullable = false)
    private String name;

    /** Maintenance interval in days; defaults are set by type in the service layer. */
    @Column(nullable = false)
    private int intervalDays;

    /** Date of the last recorded maintenance; null if never maintained. */
    private LocalDate lastMaintainedAt;

    /** Calculated next due date; null until first maintenance is scheduled. */
    private LocalDate nextDueDate;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Component() {}

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RwhSystem getSystem() { return system; }
    public void setSystem(RwhSystem system) { this.system = system; }

    public ComponentType getType() { return type; }
    public void setType(ComponentType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getIntervalDays() { return intervalDays; }
    public void setIntervalDays(int intervalDays) { this.intervalDays = intervalDays; }

    public LocalDate getLastMaintainedAt() { return lastMaintainedAt; }
    public void setLastMaintainedAt(LocalDate lastMaintainedAt) { this.lastMaintainedAt = lastMaintainedAt; }

    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }
}
