package com.pasinski.sl.backend.monitoring.user;

import com.pasinski.sl.backend.monitoring.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;

public interface UserMonitoringRepository extends JpaRepository<UserMonitoring, Long> {
    Long countByActionEquals(Action action);
    Long countByTimestampBetweenAndAction(Timestamp timestamp, Timestamp timestamp2, Action action);
}