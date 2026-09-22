package com.rwh.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rainwater harvesting system installation at a given location.
 */
@Entity
@Table(name = "systems")
public class RwhSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "System name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Location is required")
    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "system", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Component> components = new ArrayList<>();

    // ── Constructors ──────────────────────────────────────────────────────────

    public RwhSystem() {}

    public RwhSystem(String name, String location) {
        this.name = name;
        this.location = location;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<Component> getComponents() { return components; }
    public void setComponents(List<Component> components) { this.components = components; }
}
