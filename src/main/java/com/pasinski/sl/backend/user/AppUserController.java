package com.pasinski.sl.backend.user;

import com.pasinski.sl.backend.user.forms.UserForm;
import com.pasinski.sl.backend.user.forms.UserResponseForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class AppUserController {
    private AppUserService appUserService;

    @GetMapping()
    public ResponseEntity<?> getUser(@RequestParam Long idUser) {
        UserResponseForm retrievedUser;
        try {
            retrievedUser = appUserService.getUser(idUser);
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(retrievedUser, HttpStatus.OK);
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMe() {
        UserResponseForm retrievedUser;
        try {
            retrievedUser = appUserService.getMe();
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(retrievedUser, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> addUser(@Valid @RequestBody UserForm userForm) {
        try {
            appUserService.addUser(userForm);
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUser(@RequestBody UserForm userForm) {
        try {
            appUserService.updateUser(userForm);
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteUserOwnAccount() {
        try {
            appUserService.deleteUserOwnAccount();
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
