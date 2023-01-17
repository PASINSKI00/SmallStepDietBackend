package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.security.UserSecurityService;
import com.pasinski.sl.backend.user.forms.UserForm;
import com.pasinski.sl.backend.user.forms.UserResponseForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private AppUserRepository appUserRepository;
    private UserSecurityService userSecurityService;
    private PasswordEncoder passwordEncoder;

    public UserResponseForm getUser(Long idUser) {
        AppUser appUser;
        appUser = appUserRepository.findById(idUser).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        UserResponseForm userResponseForm = new UserResponseForm();
        userResponseForm.setName(appUser.getName());
        userResponseForm.setImageUrl(ApplicationConstants.DEFAULT_USER_IMAGE_URL_WITH_PARAMETER + appUser.getIdUser());

        return userResponseForm;
    }

    public UserResponseForm getMe() {
        return getUser(userSecurityService.getLoggedUserId());
    }

    public void addUser(UserForm userForm) {
        if(userSecurityService.isEmailTaken(userForm.getEmail()))
            throw new HttpClientErrorException(HttpStatus.CONFLICT);

        AppUser appUser = new AppUser(userForm.getName(), userForm.getEmail(), userForm.getPassword());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        appUserRepository.save(appUser);
    }

    public void updateUser(UserForm userForm) {
        AppUser appUser = appUserRepository.findById(userSecurityService.getLoggedUserId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

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

    public void deleteUserOwnAccount() {
        appUserRepository
                .findById(userSecurityService.getLoggedUserId())
                .ifPresent(appUser -> appUserRepository.delete(appUser));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }
}