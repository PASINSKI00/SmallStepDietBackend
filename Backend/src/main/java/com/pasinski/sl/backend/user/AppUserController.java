package com.pasinski.sl.backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.pasinski.sl.backend.user.forms.NewUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class AppUserController {
    private AppUserService appUserService;

    @GetMapping()
    public ResponseEntity<?> getUser(@RequestBody AppUser appUser) {
        AppUser retrievedUser;
        try {
            retrievedUser = appUserService.getUser(appUser.getIdUser());
            System.out.println(appUser.getIdUser());
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(retrievedUser, HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity<?> addUser(@Valid @RequestBody NewUser newUser) {
        try {
            appUserService.addUser(newUser);
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Error e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
