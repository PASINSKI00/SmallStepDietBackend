package com.pasinski.sl.backend.meal.ingredient;

import com.pasinski.sl.backend.meal.forms.IngredientForm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;
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

    public List<Ingredient> getIngredientsByNames(List<String> names) {
        return ingredientRepository.findAllByNameIn(names);
    }
}
