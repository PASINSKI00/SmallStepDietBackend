package com.pasinski.sl.backend.image;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping(
        value = "/api/image"
)
public class ImageController {
    private ImageService imageService;

    @PostMapping("/meal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMealImage(@RequestBody String image, @RequestParam Long idMeal) throws IOException {
        try {
            imageService.addMealImage(image, idMeal);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMyImage(@RequestBody String image) throws IOException {
        try {
            imageService.addMyImage(image);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }

}
