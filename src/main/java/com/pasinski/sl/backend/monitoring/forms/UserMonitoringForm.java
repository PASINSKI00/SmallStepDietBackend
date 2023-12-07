package com.pasinski.sl.backend.monitoring.forms;

public record UserMonitoringForm(Long totalNumberOfUsersCreated,
                                 Long numberOfNewUsersToday,
                                 Long numberOfNewUsersThisWeek,
                                 Long totalNumberOfUsersDeleted) {
}