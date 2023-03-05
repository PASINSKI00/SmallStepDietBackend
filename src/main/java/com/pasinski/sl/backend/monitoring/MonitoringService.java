package com.pasinski.sl.backend.monitoring;

import com.pasinski.sl.backend.monitoring.forms.UserMonitoringForm;
import com.pasinski.sl.backend.monitoring.user.UserMonitoringRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

@Service
@AllArgsConstructor
public class MonitoringService {
    private final UserMonitoringRepository userMonitoringRepository;

    public UserMonitoringForm getUserMonitoringInfo() {
        return new UserMonitoringForm(
                Math.toIntExact(userMonitoringRepository.count()),
                Math.toIntExact(userMonitoringRepository.findAllByCreatedOnBetween(
                        Timestamp.from(java.time.Instant.now().minus(1, ChronoUnit.DAYS)),
                        Timestamp.from(java.time.Instant.now())).size()),
                Math.toIntExact(userMonitoringRepository.findAllByCreatedOnBetween(
                        Timestamp.from(java.time.Instant.now().minus(7, ChronoUnit.DAYS)),
                        Timestamp.from(java.time.Instant.now())).size())
        );
    }
}