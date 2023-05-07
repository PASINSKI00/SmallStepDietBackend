package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class IngredientForm {
    @NotNull
    private String name;
    @NotNull
    private Float calories;
    @NotNull
    private Float protein;
    @NotNull
    private Float fats;
    @NotNull
    private Float carbs;
}
