package com.pasinski.sl.backend.meal.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MealResponseBody {
    private Long idMeal;
    private String name;
    private String image;
    private List<String> ingredientsNames;
    private List<String> categoriesNames;
    private Float avgRating;
    private Integer proteinRatio;
    private Integer timesUsed;
}
