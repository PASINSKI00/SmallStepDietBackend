package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.MealExtention;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class MealResponseBodyExtended {
    private String recipe;
    private int timeToPrepare;
    private int proteinRatio;
    private int carbsRatio;
    private int fatsRatio;
    private List<ReviewResponseBody> reviews;

    public MealResponseBodyExtended(MealExtention mealExtention, S3Service s3Service) {
        this.recipe = mealExtention.getRecipe();
        this.timeToPrepare = mealExtention.getTimeToPrepare();
        this.proteinRatio = mealExtention.getProteinRatio();
        this.carbsRatio = mealExtention.getCarbsRatio();
        this.fatsRatio = mealExtention.getFatsRatio();
        this.reviews = mealExtention.getReviews().stream()
                .map(review -> new ReviewResponseBody(review, s3Service)).collect(Collectors.toList());
    }
}
