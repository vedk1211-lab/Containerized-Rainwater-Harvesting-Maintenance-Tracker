package com.rwh.tracker.service;

import com.rwh.tracker.model.*;
import com.rwh.tracker.repository.AlertRepository;
import com.rwh.tracker.repository.ComponentRepository;
import com.rwh.tracker.repository.MaintenanceLogRepository;
import com.rwh.tracker.repository.SystemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service layer for managing RWH systems, components, maintenance logs, and alerts.
 */
@Service
@Transactional
public class SystemService {

    private final SystemRepository systemRepository;
    private final ComponentRepository componentRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final AlertRepository alertRepository;

    public SystemService(SystemRepository systemRepository,
                         ComponentRepository componentRepository,
                         MaintenanceLogRepository maintenanceLogRepository,
                         AlertRepository alertRepository) {
        this.systemRepository = systemRepository;
        this.componentRepository = componentRepository;
        this.maintenanceLogRepository = maintenanceLogRepository;
        this.alertRepository = alertRepository;
    }

    // ── System operations ─────────────────────────────────────────────────────

    public RwhSystem createSystem(RwhSystem system) {
        return systemRepository.save(system);
    }

    @Transactional(readOnly = true)
    public List<RwhSystem> listSystems() {
        return systemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RwhSystem getSystem(Long id) {
        return systemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("System not found: " + id));
    }

    // ── Component operations ──────────────────────────────────────────────────

    public Component addComponent(Long systemId, Component component) {
        RwhSystem system = getSystem(systemId);
        component.setSystem(system);

        if (component.getIntervalDays() <= 0 && component.getType() != null) {
            component.setIntervalDays(component.getType().getDefaultIntervalDays());
        }

        if (component.getLastMaintainedAt() != null) {
            component.setNextDueDate(
                    component.getLastMaintainedAt().plusDays(component.getIntervalDays()));
        }

        Component saved = componentRepository.save(component);
        updateAlertForComponent(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Component> listComponents(Long systemId) {
        RwhSystem system = getSystem(systemId);
        return componentRepository.findBySystemOrderByTypeAscNameAsc(system);
    }

    @Transactional(readOnly = true)
    public Component getComponent(Long componentId) {
        return componentRepository.findById(componentId)
                .orElseThrow(() -> new NoSuchElementException("Component not found: " + componentId));
    }

    @Transactional(readOnly = true)
    public List<Component> getAllComponents() {
        return componentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Component> searchComponents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllComponents();
        }
        String lowerQuery = query.toLowerCase();
        return componentRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerQuery) ||
                             c.getType().name().toLowerCase().contains(lowerQuery))
                .toList();
    }

    // ── Maintenance operations ────────────────────────────────────────────────

    public MaintenanceLog logMaintenance(Long componentId, MaintenanceLog log) {
        Component component = getComponent(componentId);
        log.setComponent(component);
        MaintenanceLog savedLog = maintenanceLogRepository.save(log);

        // Update component
        component.setLastMaintainedAt(log.getPerformedAt());
        component.setNextDueDate(log.getPerformedAt().plusDays(component.getIntervalDays()));
        componentRepository.save(component);

        // Update alert
        updateAlertForComponent(component);

        return savedLog;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceLog> getComponentHistory(Long componentId) {
        Component component = getComponent(componentId);
        return maintenanceLogRepository.findByComponentOrderByPerformedAtDesc(component);
    }

    // ── Alert operations ──────────────────────────────────────────────────────

    private void updateAlertForComponent(Component component) {
        if (component.getNextDueDate() == null) {
            return;
        }

        Alert alert = alertRepository.findByComponent(component)
                .orElseGet(() -> new Alert(component, component.getNextDueDate()));

        alert.setDueDate(component.getNextDueDate());
        
        LocalDate today = LocalDate.now();
        if (alert.getDueDate().isBefore(today)) {
            alert.setStatus(AlertStatus.OVERDUE);
        } else {
            alert.setStatus(AlertStatus.PENDING);
        }

        alertRepository.save(alert);
    }

    /**
     * Gets all components that are overdue or due soon.
     * Computes status on the fly to ensure freshness.
     */
    public List<Alert> getPendingAndOverdueAlerts() {
        List<Alert> activeAlerts = alertRepository.findByStatusInOrderByDueDateAsc(
                List.of(AlertStatus.PENDING, AlertStatus.OVERDUE));
        
        LocalDate today = LocalDate.now();
        boolean changed = false;

        for (Alert alert : activeAlerts) {
            if (alert.getDueDate().isBefore(today) && alert.getStatus() != AlertStatus.OVERDUE) {
                alert.setStatus(AlertStatus.OVERDUE);
                changed = true;
            } else if (!alert.getDueDate().isBefore(today) && alert.getStatus() != AlertStatus.PENDING) {
                alert.setStatus(AlertStatus.PENDING);
                changed = true;
            }
        }
        
        if (changed) {
            alertRepository.saveAll(activeAlerts);
        }
        
        return activeAlerts.stream()
                .filter(a -> {
                    if (a.getStatus() == AlertStatus.OVERDUE) return true;
                    // Pending, but is it due soon? (within 7 days)
                    long days = java.time.temporal.ChronoUnit.DAYS.between(today, a.getDueDate());
                    return days >= 0 && days <= 7;
                })
                .toList();
    }
}
