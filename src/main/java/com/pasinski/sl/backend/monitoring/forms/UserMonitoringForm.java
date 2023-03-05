package com.pasinski.sl.backend.monitoring.forms;

public record UserMonitoringForm(Integer totalNumberOfUsers,
                                         Integer numberOfNewUsersToday,
                                         Integer numberOfNewUsersThisWeek) {
}