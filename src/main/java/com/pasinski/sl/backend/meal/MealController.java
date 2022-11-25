package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/meal")
public class MealController {
    private final MealService mealService;

    @GetMapping("/search")
    public ResponseEntity<?> getMeals() {
        List<MealResponseBody> mealResponseBodies;

        try {
            mealResponseBodies = mealService.getMeals();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok(mealResponseBodies);
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMeal(@Valid @RequestBody MealForm mealForm) {
        try {
            mealService.addMeal(mealForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    @PutMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMeal(@RequestBody MealForm mealForm) {
        try {
            mealService.updateMeal(mealForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMeal(@RequestBody MealForm mealForm) {
        try {
            mealService.deleteMeal(mealForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().build();
    }
}
