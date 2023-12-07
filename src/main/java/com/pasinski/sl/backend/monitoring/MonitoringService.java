package com.pasinski.sl.backend.monitoring;

import com.pasinski.sl.backend.monitoring.forms.UserMonitoringForm;
import com.pasinski.sl.backend.monitoring.user.UserMonitoringRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class MonitoringService {
    private final UserMonitoringRepository userMonitoringRepository;

    public UserMonitoringForm getUserMonitoringInfo() {
        Timestamp today = Timestamp.valueOf(LocalDateTime.now());
        Timestamp dayAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(1));
        Timestamp weekAgo = Timestamp.valueOf(LocalDateTime.now().minusDays(7));

        return new UserMonitoringForm(
                userMonitoringRepository.countByActionEquals(Action.CREATE),
                userMonitoringRepository.countByTimestampBetweenAndAction(dayAgo, today, Action.CREATE),
                userMonitoringRepository.countByTimestampBetweenAndAction(weekAgo, today, Action.CREATE),
                userMonitoringRepository.countByActionEquals(Action.DELETE)
        );
    }
}