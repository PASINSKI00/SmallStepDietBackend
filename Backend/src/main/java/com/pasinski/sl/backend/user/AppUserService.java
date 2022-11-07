package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.security.UserSecurity;
import com.pasinski.sl.backend.user.forms.UserForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@AllArgsConstructor
public class AppUserService {
    private AppUserRepository appUserRepository;
    private UserSecurity userSecurity;
    public AppUser getUser(Long idUser) {
        AppUser appUser;
        appUser = appUserRepository.findById(idUser).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        appUser.setIdUser(null);
        appUser.setPassword(null);
        return appUser;
    }

    public void addUser(UserForm userForm) {
        if(userSecurity.isEmailTaken(userForm.getEmail()))
            throw new HttpClientErrorException(HttpStatus.CONFLICT);

        AppUser appUser = new AppUser(userForm.getName(), userForm.getEmail(), userForm.getPassword());

        appUserRepository.save(appUser);
    }

    public void updateUser(UserForm userForm) {
        AppUser appUser = appUserRepository.findById(userSecurity.getUserId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        applyChanges(appUser, userForm);

        appUserRepository.save(appUser);
    }

    private void applyChanges(AppUser appUser, UserForm userForm) {
        if(userForm.getName() != null && !userForm.getName().isEmpty() && !userForm.getName().isBlank())
            appUser.setName(userForm.getName());

        if (userForm.getEmail() != null && !userForm.getEmail().isBlank() && !userForm.getEmail().isEmpty())
            appUser.setEmail(userForm.getEmail());

        if (userForm.getPassword() != null && !userForm.getPassword().isBlank() && !userForm.getPassword().isEmpty())
            appUser.setPassword(userForm.getPassword());

        if (userForm.getImage() != null && !userForm.getImage().isBlank() && !userForm.getImage().isEmpty())
            appUser.setImage(userForm.getImage());
    }
}