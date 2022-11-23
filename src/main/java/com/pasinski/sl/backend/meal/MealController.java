package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

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
    public ResponseEntity<?> addMeal(MealForm mealForm) {
        try {
            mealService.addMeal(mealForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }
}
