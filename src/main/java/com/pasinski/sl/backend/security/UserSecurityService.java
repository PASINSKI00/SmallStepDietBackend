package com.pasinski.sl.backend.security;

import com.pasinski.sl.backend.user.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSecurityService {

    private AppUserRepository appUserRepository;

    public boolean isEmailTaken(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }

    public boolean isUserLoggedIn() {
        //TODO: implement
        return true;
    }

    public Long getLoggedUserId() {
        //TODO: implement
        return 1L;
    }
}
