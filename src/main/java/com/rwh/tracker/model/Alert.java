package com.rwh.tracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Represents a maintenance alert for a specific component.
 */
@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "component_id", nullable = false)
    private Component component;

    @NotNull
    @Column(nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AlertStatus status = AlertStatus.PENDING;

    public Alert() {}

    public Alert(Component component, LocalDate dueDate) {
        this.component = component;
        this.dueDate = dueDate;
        this.status = AlertStatus.PENDING;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Component getComponent() { return component; }
    public void setComponent(Component component) { this.component = component; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public AlertStatus getStatus() { return status; }
    public void setStatus(AlertStatus status) { this.status = status; }
}
