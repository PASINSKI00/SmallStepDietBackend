package com.pasinski.sl.backend.monitoring.user;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserMonitoringRepository extends JpaRepository<UserMonitoring, Long> {
}