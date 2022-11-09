package com.pasinski.sl.backend.security;

import com.pasinski.sl.backend.user.AppUserRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserSecurity {

    private AppUserRepository appUserRepository;

    public boolean isEmailTaken(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }

    public boolean isUserLoggedIn() {
        //TODO: implement
        return true;
    }

    public Long getUserId() {
        //TODO: implement
        return 1L;
    }
}
