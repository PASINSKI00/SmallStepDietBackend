package com.pasinski.sl.backend.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

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
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(retrievedUser, HttpStatus.OK);
    }
}
