package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
public class MealForm {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Recipe is mandatory")
    private String recipe;

    @NotEmpty(message = "Ingredients are mandatory")
    private Set<Long> ingredientsIds;

    @NotNull(message = "Time to prepare is mandatory")
    private Integer timeToPrepare;

    private String image;
    private Set<Long> categoriesIds;
}
