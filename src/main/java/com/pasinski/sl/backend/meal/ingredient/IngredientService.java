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
        ingredientRepository.save(new Ingredient(ingredientForm));
    }
}
