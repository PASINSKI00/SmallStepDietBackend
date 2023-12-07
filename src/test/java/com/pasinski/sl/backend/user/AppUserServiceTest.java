package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.email.EmailSenderService;
import com.pasinski.sl.backend.email.confirmationToken.EmailConfirmationTokenService;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.user.forms.UserResponseForm;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private UserSecurityService userSecurityService;

    @InjectMocks
    private AppUserService appUserService;

    @Test
    @Disabled
    void getUser_returnCorrectUser_True() {
        // given
        AppUser user1 = new AppUser("user1", "email1", "password1");
        user1.setIdUser(1L);
        AppUser user2 = new AppUser("user2", "email2", "password2");
        user2.setIdUser(2L);

        // when
        when(appUserRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(appUserRepository.findById(2L)).thenReturn(Optional.of(user2));

        // then
        assertEquals(user1, appUserService.getUser(1L));
        assertEquals(user2, appUserService.getUser(2L));
    }

    @Test
    @Disabled
    void getMe() {
        // Given
        AppUser user1 = new AppUser("user1", "email1", "password1");
        user1.setIdUser(1L);
        UserResponseForm actual;

        // When
        when(userSecurityService.getLoggedUser()).thenReturn(user1);
        actual = appUserService.getMe();

        // Then
        assertEquals(user1, actual);
    }
}