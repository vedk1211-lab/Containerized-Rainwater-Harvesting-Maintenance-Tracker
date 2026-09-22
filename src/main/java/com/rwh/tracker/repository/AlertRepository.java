package com.rwh.tracker.repository;

import com.rwh.tracker.model.Alert;
import com.rwh.tracker.model.AlertStatus;
import com.rwh.tracker.model.Component;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    Optional<Alert> findByComponent(Component component);
    List<Alert> findByStatusInOrderByDueDateAsc(List<AlertStatus> statuses);
}
