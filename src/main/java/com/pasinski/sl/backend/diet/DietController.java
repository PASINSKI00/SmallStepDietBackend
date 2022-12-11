package com.pasinski.sl.backend.diet;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/diet")
@AllArgsConstructor
public class DietController {
    private final DietService dietService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiet(@RequestParam Long idDiet) {
        Diet diet;
        try {
            diet = this.dietService.getDiet(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(diet, HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addDiet(@RequestBody Long[][] days) {
        Long id;
        try {
            id = this.dietService.addDiet(days);
        } catch (HttpClientErrorException e){
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateDiet(){
        //TODO
        return null;
    }

    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteDiet(){
        //TODO
        return null;
    }

    @GetMapping("/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateDietPDF(@RequestParam Long idDiet) {
        String fileName;
        try {
            fileName = this.dietService.generateDietPDF(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(fileName, HttpStatus.OK);
    }
}
