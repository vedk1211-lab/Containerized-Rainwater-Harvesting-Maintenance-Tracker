package com.rwh.tracker.repository;

import com.rwh.tracker.model.RwhSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link RwhSystem} entities.
 */
@Repository
public interface SystemRepository extends JpaRepository<RwhSystem, Long> {
}
