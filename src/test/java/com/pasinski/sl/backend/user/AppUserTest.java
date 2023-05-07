package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.user.accessManagment.Role;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AppUserTest {

    @Test
    void getAuthorities_hasAdmin_False() {
//        Given
        AppUser user = new AppUser();
        user.setRoles(Collections.emptySet());
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        boolean actual;

//        When
        actual = user.getAuthorities().stream().anyMatch(authority -> authority.equals(adminAuthority));

//        Then
        assertFalse(actual);
    }

    @Test
    void getAuthorities_hasAdmin_True() {
//        Given
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        AppUser admin = new AppUser();
        admin.setRoles(Collections.singleton(adminRole));

        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        boolean actual;

//        When
        actual = admin.getAuthorities().stream().anyMatch(authority -> authority.equals(adminAuthority));

//        Then
        assertTrue(actual);
    }

    @Test
    void getAuthorities_hasUser_False() {
//        Given
        AppUser user = new AppUser();
        user.setRoles(Collections.emptySet());
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        boolean actual;

//        When
        actual = user.getAuthorities().stream().anyMatch(authority -> authority.equals(userAuthority));

//        Then
        assertFalse(actual);
    }

    @Test
    void getAuthorities_hasUser_True() {
//        Given
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        AppUser user = new AppUser();
        user.setRoles(Collections.singleton(userRole));

        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        boolean actual;

//        When
        actual = user.getAuthorities().stream().anyMatch(authority -> authority.equals(userAuthority));

//        Then
        assertTrue(actual);
    }

    @Test
    void getAuthorities_returnsSimpleGrantedAuthority_True() {
//        Given
        Role userRole = new Role();
        userRole.setName("ROLE_USER");
        AppUser user = new AppUser();
        user.setRoles(Collections.singleton(userRole));

        boolean actual;

//        When
        actual = user.getAuthorities().stream().anyMatch(authority -> authority instanceof SimpleGrantedAuthority);

//        Then
        assertTrue(actual);
    }
}
