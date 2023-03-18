package com.pasinski.sl.backend.config.security;

import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserSecurityService {

    private AppUserRepository appUserRepository;

    public boolean isEmailTaken(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }

    public Long getLoggedUserId() {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return appUser.getIdUser();
    }

    public AppUser getLoggedUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public void setImageSetBooleanValue(boolean value) {
        AppUser appUser = getLoggedUser();
        appUser.setImageSet(value);
        appUserRepository.save(appUser);
    }
}
