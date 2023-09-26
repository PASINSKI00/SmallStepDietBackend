package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.forms.DietResponseForm;
import com.pasinski.sl.backend.diet.forms.Grocery;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/api/diet")
@AllArgsConstructor
public class DietController {
    private final DietService dietService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDiet(@RequestParam Long idDiet) {
        DietResponseForm dietResponseForm;
        try {
            dietResponseForm = this.dietService.getDiet(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(dietResponseForm, HttpStatus.OK);
    }

    @GetMapping("/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateDietPDF(@RequestParam Long idDiet) {
        String url;
        try {
            url = this.dietService.generateDietPDF(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/pdf/url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getDietPDFUrl(@RequestParam Long idDiet) {
        String url;
        try {
            url = this.dietService.getDietPDFUrl(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/groceries")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGroceries(@RequestParam Long idDiet) {
        List<Grocery> groceries;
        try {
            groceries = this.dietService.getGroceries(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(groceries, HttpStatus.OK);
    }

    @GetMapping("/groceries/pdf")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> generateGroceriesPDF(@RequestParam Long idDiet) {
        String url;
        try {
            url = this.dietService.generateGroceriesPDF(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(url, HttpStatus.OK);
    }

    @GetMapping("/groceries/pdf/url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGroceriesPDFUrl(@RequestParam Long idDiet) {
        String url;
        try {
            url = this.dietService.getGroceriesPDFUrl(idDiet);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }

        return new ResponseEntity<>(url, HttpStatus.OK);
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
    public ResponseEntity<?> modifyFinalDiet(@RequestBody DietResponseForm modifiedDiet) {
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
}
