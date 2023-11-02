package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealExtention;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MealResponseBodyExtended extends MealResponseBody {
    private final String recipe;
    private final int timeToPrepare;
    private final int carbsRatio;
    private final int fatsRatio;
    private final List<ReviewResponseBody> reviews;

    public MealResponseBodyExtended(Meal meal, S3Service s3Service) {
        super(meal, s3Service);
        MealExtention mx = meal.getMealExtention();
        this.recipe = mx.getRecipe();
        this.timeToPrepare = mx.getTimeToPrepare();
        this.carbsRatio = mx.getCarbsRatio();
        this.fatsRatio = mx.getFatsRatio();
        this.reviews = mx.getReviews().stream()
                .map(review -> new ReviewResponseBody(review, s3Service)).collect(Collectors.toList());
    }
}
