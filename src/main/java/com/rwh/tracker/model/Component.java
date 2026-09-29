package com.rwh.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single component in a rainwater harvesting system
 * (e.g., a tank, filter, pump, etc.).
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

    @NotNull(message = "Component type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComponentType type;

    @NotBlank(message = "Component name is required")
    @Column(nullable = false)
    private String name;

    /**
     * Maintenance interval in days.
     * Defaults to the type's default if left at 0 when adding.
     */
    @Column(nullable = false)
    private int intervalDays;

    /** Date the component was last maintained. */
    private LocalDate lastMaintainedAt;

    /** Calculated next due date (lastMaintainedAt + intervalDays). */
    private LocalDate nextDueDate;

    @OneToMany(mappedBy = "component", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("performedAt DESC")
    private List<MaintenanceLog> maintenanceLogs = new ArrayList<>();

    // ── Constructors ──────────────────────────────────────────────────────────

    public Component() {}

    public Component(String name, ComponentType type) {
        this.name = name;
        this.type = type;
        this.intervalDays = type.getDefaultIntervalDays();
    }

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

    public List<MaintenanceLog> getMaintenanceLogs() { return maintenanceLogs; }
    public void setMaintenanceLogs(List<MaintenanceLog> maintenanceLogs) { this.maintenanceLogs = maintenanceLogs; }
}
