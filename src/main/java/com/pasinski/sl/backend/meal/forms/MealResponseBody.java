package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.mealIngredient.MealIngredient;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MealResponseBody {
    private final Long idMeal;
    private final String name;
    private final String imageUrl;
    private final List<String> ingredientsNames;
    private final List<String> categoriesNames;
    private final Float avgRating;
    private final Integer proteinRatio;
    private final Integer timesUsed;

    public MealResponseBody(Meal meal, S3Service s3Service) {
        this.idMeal = meal.getIdMeal();
        this.name = meal.getName();
        this.ingredientsNames = meal.getIngredients().stream().map(MealIngredient::getIngredient)
                .map(Ingredient::getName).collect(Collectors.toList());
        String mealImageName = meal.isImageSet() ?
                ApplicationConstants.getMealImageName(meal.getIdMeal()) :
                ApplicationConstants.DEFAULT_MEAL_IMAGE_NAME;
        this.imageUrl = s3Service.getFileUrl(mealImageName, FileType.MEAL_IMAGE);
        this.categoriesNames = meal.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        this.avgRating = meal.getAvgRating();
        this.proteinRatio = meal.getMealExtention().getProteinRatio();
        this.timesUsed = meal.getTimesUsed();
    }
}
