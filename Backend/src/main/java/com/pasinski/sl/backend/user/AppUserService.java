package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.user.forms.NewUser;
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

    public void addUser(NewUser newUser) {
        if(isEmailTaken(newUser.getEmail()))
            throw new HttpClientErrorException(HttpStatus.CONFLICT);

        AppUser appUser = new AppUser(newUser.getName(), newUser.getEmail(), newUser.getPassword());

        appUserRepository.save(appUser);
    }

    private boolean isEmailTaken(String email) {
        return appUserRepository.findByEmail(email).isPresent();
    }
}