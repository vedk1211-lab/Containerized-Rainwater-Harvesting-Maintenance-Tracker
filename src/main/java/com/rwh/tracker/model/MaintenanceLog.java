package com.rwh.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Log entry representing a single maintenance action performed on a component.
 */
@Entity
@Table(name = "maintenance_logs")
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @NotNull(message = "Date performed is required")
    @Column(nullable = false)
    private LocalDate performedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    private String photoUrl;

    public MaintenanceLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Component getComponent() { return component; }
    public void setComponent(Component component) { this.component = component; }

    public LocalDate getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDate performedAt) { this.performedAt = performedAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
