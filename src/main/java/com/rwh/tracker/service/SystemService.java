package com.rwh.tracker.service;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.RwhSystem;
import com.rwh.tracker.repository.ComponentRepository;
import com.rwh.tracker.repository.SystemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service layer for managing RWH systems and their components.
 */
@Service
@Transactional
public class SystemService {

    private final SystemRepository systemRepository;
    private final ComponentRepository componentRepository;

    public SystemService(SystemRepository systemRepository,
                         ComponentRepository componentRepository) {
        this.systemRepository = systemRepository;
        this.componentRepository = componentRepository;
    }

    // ── System operations ─────────────────────────────────────────────────────

    /**
     * Persists a new system.
     *
     * @param system the system to create (id must be null)
     * @return the saved entity with generated id
     */
    public RwhSystem createSystem(RwhSystem system) {
        return systemRepository.save(system);
    }

    /**
     * Returns all registered systems in insertion order.
     */
    @Transactional(readOnly = true)
    public List<RwhSystem> listSystems() {
        return systemRepository.findAll();
    }

    /**
     * Returns a single system by id.
     *
     * @throws NoSuchElementException if not found
     */
    @Transactional(readOnly = true)
    public RwhSystem getSystem(Long id) {
        return systemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("System not found: " + id));
    }

    // ── Component operations ──────────────────────────────────────────────────

    /**
     * Adds a component to the given system, applying a default {@code intervalDays}
     * if none has been set (or if it is ≤ 0).
     *
     * @param systemId  id of the parent system
     * @param component the component to create
     * @return the saved entity
     */
    public Component addComponent(Long systemId, Component component) {
        RwhSystem system = getSystem(systemId);
        component.setSystem(system);

        // Apply default interval based on component type when not explicitly provided
        if (component.getIntervalDays() <= 0 && component.getType() != null) {
            component.setIntervalDays(component.getType().getDefaultIntervalDays());
        }

        // Calculate nextDueDate from lastMaintainedAt if available
        if (component.getLastMaintainedAt() != null) {
            component.setNextDueDate(
                    component.getLastMaintainedAt().plusDays(component.getIntervalDays()));
        }

        return componentRepository.save(component);
    }

    /**
     * Returns all components for the given system, ordered by type then name.
     */
    @Transactional(readOnly = true)
    public List<Component> listComponents(Long systemId) {
        RwhSystem system = getSystem(systemId);
        return componentRepository.findBySystemOrderByTypeAscNameAsc(system);
    }
}
