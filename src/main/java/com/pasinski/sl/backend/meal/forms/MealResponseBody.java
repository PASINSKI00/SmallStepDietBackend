package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.mealIngredient.MealIngredient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class MealResponseBody {
    private Long idMeal;
    private String name;
    private String imageUrl;
    private List<String> ingredientsNames;
    private List<String> categoriesNames;
    private Float avgRating;
    private Integer proteinRatio;
    private Integer timesUsed;

    public MealResponseBody(Meal meal, S3Service s3Service) {
        this.idMeal = meal.getIdMeal();
        this.name = meal.getName();
        this.ingredientsNames = meal.getIngredients().stream().map(MealIngredient::getIngredient)
                .map(Ingredient::getName).collect(Collectors.toList());
        this.imageUrl = s3Service.getFileUrl("meal_id_" + meal.getIdMeal() + ".jpg", FileType.MEAL_IMAGE);
        this.categoriesNames = meal.getCategories().stream().map(Category::getName).collect(Collectors.toList());
        this.avgRating = meal.getAvgRating();
        this.proteinRatio = meal.getMealExtention().getProteinRatio();
        this.timesUsed = meal.getTimesUsed();
    }
}
