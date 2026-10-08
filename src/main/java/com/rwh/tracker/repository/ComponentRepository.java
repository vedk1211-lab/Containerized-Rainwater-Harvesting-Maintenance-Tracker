package com.rwh.tracker.repository;

import com.rwh.tracker.model.Component;
import com.rwh.tracker.model.RwhSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Component} entities.
 */
@Repository
public interface ComponentRepository extends JpaRepository<Component, Long> {

    /** Returns all components belonging to the given system, ordered by type then name. */
    List<Component> findBySystemOrderByTypeAscNameAsc(RwhSystem system);

    /** Eagerly fetches all components with their system (avoids N+1). */
    @Query("SELECT c FROM Component c JOIN FETCH c.system ORDER BY c.type ASC, c.name ASC")
    List<Component> findAllWithSystem();

    @Query("SELECT c FROM Component c JOIN FETCH c.system WHERE c.id = :id")
    java.util.Optional<Component> findByIdWithSystem(@org.springframework.data.repository.query.Param("id") Long id);
}
