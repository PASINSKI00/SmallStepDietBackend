package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class IngredientForm {
    @NotNull
    private String name;
    @NotNull
    private Integer calories;
    @NotNull
    private Integer protein;
    @NotNull
    private Integer fats;
    @NotNull
    private Integer carbs;
}
