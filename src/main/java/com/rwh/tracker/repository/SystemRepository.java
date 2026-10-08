package com.rwh.tracker.repository;

import com.rwh.tracker.model.RwhSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link RwhSystem} entities.
 */
@Repository
public interface SystemRepository extends JpaRepository<RwhSystem, Long> {
    
    @Query("SELECT DISTINCT s FROM RwhSystem s LEFT JOIN FETCH s.components")
    List<RwhSystem> findAllWithComponents();
}
