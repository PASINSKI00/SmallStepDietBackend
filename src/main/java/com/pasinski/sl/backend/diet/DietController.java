package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.forms.DietResponseForm;
import com.pasinski.sl.backend.diet.forms.request.DietUnauthenticatedRequestForm;
import com.pasinski.sl.backend.diet.forms.Grocery;
import com.pasinski.sl.backend.diet.forms.request.FinalDietModifyRequestForm;
import com.pasinski.sl.backend.diet.forms.request.FinalDietModifyUnauthenticatedRequestForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/diet")
@AllArgsConstructor
public class DietController {
    private final DietService dietService;

    @GetMapping()
    public ResponseEntity<?> getDiet(@RequestParam Long idDiet) {
        DietResponseForm dietResponseForm;
        try {
            dietResponseForm = this.dietService.getDiet(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(dietResponseForm, HttpStatus.OK);
    }

    @GetMapping("/groceries")
    public ResponseEntity<?> getGroceries(@RequestParam Long idDiet) {
        List<Grocery> groceries;
        try {
            groceries = this.dietService.getGroceries(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(groceries, HttpStatus.OK);
    }

    @GetMapping("/mine")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyDiets() {
        List<DietResponseForm> diets;
        try {
            diets = this.dietService.getMyDiets();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(diets, HttpStatus.OK);
    }

    @GetMapping("/mine/meals/unreviewed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUnreviewedMealsUsedByUser() {
        List<MealResponseBody> meals;
        try {
            meals = this.dietService.getUnreviewedMealsUsedByUser();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(meals, HttpStatus.OK);
    }

    @GetMapping("/mine/meals/reviewed")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAlreadyReviewedMealsUsedByUser() {
        List<MealResponseBody> meals;
        try {
            meals = this.dietService.getAlreadyReviewedMealsUsedByUser();
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(meals, HttpStatus.OK);
    }

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addDiet(@RequestBody List<List<Long>> days) {
        Long id;
        try {
            id = this.dietService.addDiet(days);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateDiet(@RequestParam Long idDiet, @RequestBody List<List<Long>> days) {
        try {
            this.dietService.updateDiet(idDiet, days);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/final")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> modifyFinalDiet(@RequestBody FinalDietModifyRequestForm modifiedDiet) {
        try {
            this.dietService.modifyFinalDiet(modifiedDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/final/day/reset")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> resetDay(@RequestParam Long idDiet, @RequestParam Long idFinalDay) {
        try {
            this.dietService.resetDay(idDiet, idFinalDay);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/final/recalculate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> reCalculate(@RequestParam Long idDiet) {
        try {
            this.dietService.reCalculate(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteDiet(@RequestParam Long idDiet) {
        try {
            this.dietService.deleteDiet(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    // ---------- UNAUTHENTICATED ----------
    @PostMapping("/unauthenticated")
    public ResponseEntity<?> addDietForUnauthenticated(@Valid @RequestBody DietUnauthenticatedRequestForm requestForm) {
        Long id;
        try {
            id = this.dietService.addDietForUnauthenticated(requestForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping("/unauthenticated")
    public ResponseEntity<?> updateDietForUnauthenticated(@RequestParam Long idDiet,
                                                          @Valid @RequestBody DietUnauthenticatedRequestForm requestForm) {
        try {
            this.dietService.updateDietForUnauthenticated(idDiet, requestForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/final/unauthenticated")
    public ResponseEntity<?> modifyFinalDietForUnauthenticated(@Valid @RequestBody FinalDietModifyUnauthenticatedRequestForm requestForm) {
        try {
            this.dietService.modifyFinalDietForUnauthenticated(requestForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/final/day/reset/unauthenticated")
    public ResponseEntity<?> resetDayForUnauthenticated(@RequestParam Long idDiet, @RequestParam Long idFinalDay,
                                                        @Valid @RequestBody BodyInfoForm bodyInfoForm) {
        try {
            this.dietService.resetDay(idDiet, idFinalDay, bodyInfoForm);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
