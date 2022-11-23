package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.category.CategoryRepository;
import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;

    public List<MealResponseBody> getMeals() {
        List<Meal> meals = mealRepository.findAll();
        List<MealResponseBody> mealResponseBodies = new ArrayList<>();

        meals.forEach(meal -> {
            mealResponseBodies.add(new MealResponseBody(
                    meal.getIdMeal(),
                    meal.getName(),
                    meal.getImage(),
                    meal.getIngredients().stream().map(Ingredient::getName).toList(),
                    meal.getCategories().stream().map(Category::getName).toList()
        ));
        });

        return mealResponseBodies;
    }

    public void addMeal(MealForm mealForm) {
        Meal meal = new Meal();

        meal.setName(mealForm.getName());
//        meal.setRecipe(mealForm.getRecipe());

        mealForm.getIngredientsIds().forEach(id -> {
            Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
            meal.getIngredients().add(ingredient);
        });

        mealForm.getCategoriesIds().forEach(id -> {
            Category category = categoryRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
            meal.getCategories().add(category);
        });

        mealRepository.save(meal);
    }
}
