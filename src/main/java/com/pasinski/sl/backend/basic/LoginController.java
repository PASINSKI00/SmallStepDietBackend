package com.pasinski.sl.backend.basic;

import com.pasinski.sl.backend.config.security.UserSecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class LoginController {

    private UserSecurityService userSecurityService;

    @GetMapping("/api/login")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> login() {
        Long userId = userSecurityService.getLoggedUserId();
        return new ResponseEntity<Long>(userId, HttpStatus.OK);
    }
}
