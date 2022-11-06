package com.pasinski.sl.backend.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@AllArgsConstructor
public class AppUserService {
    private AppUserRepository appUserRepository;
    public AppUser getUser(Long idUser) {
        AppUser appUser;
        appUser = appUserRepository.findById(idUser).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        appUser.setIdUser(null);
        appUser.setPassword(null);
        return appUser;
    }
}