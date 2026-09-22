package com.rwh.tracker.repository;

import com.rwh.tracker.model.MaintenanceLog;
import com.rwh.tracker.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {
    List<MaintenanceLog> findByComponentOrderByPerformedAtDesc(Component component);
}
