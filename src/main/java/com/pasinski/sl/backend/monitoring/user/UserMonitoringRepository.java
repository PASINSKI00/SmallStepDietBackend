package com.pasinski.sl.backend.monitoring.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Collection;

public interface UserMonitoringRepository extends JpaRepository<UserMonitoring, Long> {
    Collection<Object> findAllByCreatedOnBetween(Timestamp createdOn, Timestamp createdOn2);
}