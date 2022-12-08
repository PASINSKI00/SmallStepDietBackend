package com.pasinski.sl.backend.meal.ingredient;

import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.forms.IngredientForm;
import com.pasinski.sl.backend.meal.forms.IngredientWithMealSpecifics;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final MealRepository mealRepository;
    public List<Ingredient> getAllIngredients() {
        return ingredientRepository.getAllIdsAndNames();
    }

    public void addIngredient(IngredientForm ingredientForm) {
        Ingredient ingredient = new Ingredient();

        ingredient.setName(ingredientForm.getName());
        ingredient.setCalories(ingredientForm.getCalories());
        ingredient.setProtein(ingredientForm.getProtein());
        ingredient.setCarbs(ingredientForm.getCarbs());
        ingredient.setFats(ingredientForm.getFats());

        ingredientRepository.save(ingredient);
    }

    public List<IngredientWithMealSpecifics> getIngredientsForMeal(Long idMeal) {
        List<IngredientWithMealSpecifics> ingredientsWithMealSpecifics = new ArrayList<>();
        Meal meal = mealRepository.findById(idMeal).orElseThrow(() -> new IllegalArgumentException("Meal not found"));
        meal.getIngredients().forEach((ingredient, mealIngredientSpecifics) -> {
            ingredientsWithMealSpecifics.add(new IngredientWithMealSpecifics(
                    ingredient.getIdIngredient(),
                    ingredient.getName(),
                    ingredient.getCalories(),
                    ingredient.getProtein(),
                    ingredient.getCarbs(),
                    ingredient.getFats(),
                    mealIngredientSpecifics.getWeight(),
                    mealIngredientSpecifics.getInitialRatioInMeal()
            ));
        });

        return ingredientsWithMealSpecifics;
    }
}
