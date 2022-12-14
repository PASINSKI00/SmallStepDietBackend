package com.pasinski.sl.backend.meal.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IngredientWithMealSpecifics {
    private Long idIngredient;
    private String name;
    private Integer calories;
    private Integer protein;
    private Integer carbs;
    private Integer fats;
    private Integer weight;
    private Integer initialRatioInMeal;
}
