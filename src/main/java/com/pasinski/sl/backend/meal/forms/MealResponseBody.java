package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MealResponseBody {
    private Long idMeal;
    private String name;
    private String image;
    private List<String> ingredientNames;
    private List<String> categoriesNames;
}
