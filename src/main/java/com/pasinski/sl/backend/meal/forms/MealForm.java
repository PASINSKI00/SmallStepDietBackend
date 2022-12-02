package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
public class MealForm {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Recipe is mandatory")
    private String recipe;

    @NotNull(message = "Ingredients are mandatory")
    private Map<Long, Integer> ingredients;

    @NotNull(message = "Time to prepare is mandatory")
    private Integer timeToPrepare;

    private Long idMeal;
    private String image;
    private List<Long> categoriesIds;
}
