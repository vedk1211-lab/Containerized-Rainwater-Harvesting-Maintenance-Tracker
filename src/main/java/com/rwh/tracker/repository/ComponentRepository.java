package com.rwh.tracker.repository;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.RwhSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Component} entities.
 */
@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {

    /** Returns all components belonging to the given system, ordered by type then name. */
    List<Component> findBySystemOrderByTypeAscNameAsc(RwhSystem system);
}
