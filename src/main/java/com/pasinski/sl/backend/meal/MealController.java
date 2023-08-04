package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.forms.MealResponseBodyExtended;
import com.pasinski.sl.backend.meal.forms.ReviewForm;
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
    public ResponseEntity<?> getMeals(@RequestParam(required = false, defaultValue = "") String nameContains,
                                      @RequestParam(required = false, defaultValue = "") String sortBy,
                                      @RequestParam(required = false, defaultValue = "") List<String> categories,
                                      @RequestParam(required = false, defaultValue = "0") int pageNumber,
                                      @RequestParam(required = false, defaultValue = "15") int pageSize) {
        List<MealResponseBody> mealResponseBodies;

        try {
            mealResponseBodies = mealService.getMeals(nameContains, sortBy, categories, pageNumber, pageSize);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok(mealResponseBodies);
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addMeal(@Valid @RequestBody MealForm mealForm) {
        Long idMeal;
        try {
            idMeal = mealService.addMeal(mealForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(idMeal, HttpStatus.CREATED);
    }

    @PostMapping("/review")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addReview(@RequestBody ReviewForm reviewForm) {
        try {
            mealService.addReview(reviewForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMeal(@RequestBody MealForm mealForm) {
        try {
            mealService.updateMeal(mealForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteMeal(@RequestParam Long idMeal) {
        try {
            mealService.deleteMeal(idMeal);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/extend")
    public ResponseEntity<?> extendMeal(@RequestParam Long idMeal) {
        MealResponseBodyExtended mealResponseBodyExtended;

        try {
            mealResponseBodyExtended = mealService.extendMeal(idMeal);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return ResponseEntity.ok(mealResponseBodyExtended);
    }

}
