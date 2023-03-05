package com.pasinski.sl.backend.monitoring;

import com.pasinski.sl.backend.monitoring.forms.UserMonitoringForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@RestController
@RequestMapping("/api/monitoring")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@AllArgsConstructor
public class MonitoringController {
    private final MonitoringService monitoringService;

    @GetMapping()
    public ResponseEntity<?> getUserMonitoringInfo() {
        UserMonitoringForm applicationInformationForm;

        try {
            applicationInformationForm = monitoringService.getUserMonitoringInfo();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(applicationInformationForm, HttpStatus.OK);
    }
}