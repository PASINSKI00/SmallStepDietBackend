package com.pasinski.sl.backend.image;

import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping(
        value = "/api/image"
)
public class ImageController {
    private ImageService imageService;

    @GetMapping(
            value = "/meal",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public ResponseEntity<InputStreamResource> getMealImage(@RequestParam @Valid String name) throws IOException {
        InputStreamResource inputStreamResource;
        try {
            inputStreamResource = imageService.getMealImage(name);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(inputStreamResource);
    }

    @PostMapping("/meal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMealImage(@RequestParam MultipartFile image, @RequestParam Long idMeal) throws IOException {
        try {
            imageService.addMealImage(image, idMeal);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }
}
