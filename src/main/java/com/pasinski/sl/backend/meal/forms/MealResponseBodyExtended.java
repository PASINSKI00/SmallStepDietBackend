package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealExtention;
import com.pasinski.sl.backend.meal.review.Review;
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

    public MealResponseBodyExtended(Meal meal, MealExtention mealExtention) {
        this.recipe = mealExtention.getRecipe();
        this.timeToPrepare = mealExtention.getTimeToPrepare();
        this.proteinRatio = mealExtention.getProteinRatio();
        this.carbsRatio = mealExtention.getCarbsRatio();
        this.fatsRatio = mealExtention.getFatsRatio();
        List<Review> reviews = mealExtention.getReviews();

        this.reviews = reviews.stream().map(
                review -> new ReviewResponseBody(
                        ApplicationConstants.DEFAULT_USER_IMAGE_URL_WITH_PARAMETER + review.getAuthor().getIdUser(),
                        review.getAuthor().getName(),
                        review.getRating(),
                        review.getComment())).collect(Collectors.toList());
    }
}
