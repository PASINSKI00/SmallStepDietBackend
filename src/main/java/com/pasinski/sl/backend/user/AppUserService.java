package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.email.EmailSenderService;
import com.pasinski.sl.backend.email.confirmationToken.EmailConfirmationToken;
import com.pasinski.sl.backend.email.confirmationToken.EmailConfirmationTokenService;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.user.accessManagment.Privilege;
import com.pasinski.sl.backend.user.accessManagment.Role;
import com.pasinski.sl.backend.user.forms.UserForm;
import com.pasinski.sl.backend.user.forms.UserResponseForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AppUserService implements UserDetailsService {
    private AppUserRepository appUserRepository;
    private MealRepository mealRepository;
    private UserSecurityService userSecurityService;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final EmailSenderService emailSenderService;
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
        if (userSecurityService.isEmailTaken(userForm.getEmail()))
            throw new HttpClientErrorException(HttpStatus.CONFLICT);

        AppUser appUser = new AppUser(userForm.getName(), userForm.getEmail(), userForm.getPassword());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));

        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
                token,
                appUser
        );

        emailConfirmationTokenService.saveEmailConfirmationToken(emailConfirmationToken);

        emailSenderService.emailAddressVerification(appUser, emailConfirmationToken);
    }

    public void updateUser(UserForm userForm) {
        AppUser appUser = appUserRepository.findById(userSecurityService.getLoggedUserId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        applyChanges(appUser, userForm);

        appUserRepository.save(appUser);
    }

    private void applyChanges(AppUser appUser, UserForm userForm) {
        if (userForm.getName() != null && !userForm.getName().isEmpty() && !userForm.getName().isBlank())
            appUser.setName(userForm.getName());

        if (userForm.getEmail() != null && !userForm.getEmail().isBlank() && !userForm.getEmail().isEmpty())
            appUser.setEmail(userForm.getEmail());

        if (userForm.getPassword() != null && !userForm.getPassword().isBlank() && !userForm.getPassword().isEmpty())
            appUser.setPassword(userForm.getPassword());
    }

    public void deleteUserOwnAccount() {
        AppUser appUser = userSecurityService.getLoggedUser();

        mealRepository.findAllByAuthor(appUser).forEach(meal -> {
            meal.setAuthor(null);
            mealRepository.save(meal);
        });

        appUserRepository.delete(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return this.appUserRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(String.format("User with email %s not found", email)));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(
            Collection<Role> roles) {

        return getGrantedAuthorities(getPrivileges(roles));
    }

    private List<String> getPrivileges(Collection<Role> roles) {

        List<String> privileges = new ArrayList<>();
        List<Privilege> collection = new ArrayList<>();
        for (Role role : roles) {
            privileges.add(role.getName());
            collection.addAll(role.getPrivileges());
        }
        for (Privilege item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private List<GrantedAuthority> getGrantedAuthorities(List<String> privileges) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    public void verifyUserEmail(String token) {
        EmailConfirmationToken emailConfirmationToken = emailConfirmationTokenService.findEmailConfirmationTokenByToken(token)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        AppUser appUser = emailConfirmationToken.getAppUser();
        appUser.setEmailVerified(true);

        appUserRepository.save(appUser);
    }
}