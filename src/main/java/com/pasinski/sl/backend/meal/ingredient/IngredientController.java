package com.pasinski.sl.backend.meal.ingredient;

import com.pasinski.sl.backend.meal.forms.IngredientForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
@AllArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllIngredients() {
        List<Ingredient> ingredients;

        try {
            ingredients = ingredientService.getAllIngredients();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok(ingredients);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addIngredient(@Valid @RequestBody IngredientForm ingredientForm) {
        try {
            ingredientService.addIngredient(ingredientForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
