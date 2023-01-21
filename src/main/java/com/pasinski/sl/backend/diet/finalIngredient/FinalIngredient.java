package com.pasinski.sl.backend.diet.finalIngredient;

import com.pasinski.sl.backend.diet.forms.FinalIngredientResponseForm;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.meal.mealIngredientSpecifics.MealIngredientSpecifics;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FinalIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_final_ingredient", nullable = false)
    private Long idFinalIngredient;

    @ManyToOne
    private Ingredient ingredient;
    private Integer initialWeight;

    private Integer weight;
    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;

    public FinalIngredient(Ingredient ingredient, MealIngredientSpecifics specifics, Float ingredientWeightMultiplier) {
        this.ingredient = ingredient;
        this.initialWeight = specifics.getInitialWeight();

        this.weight = (int) (initialWeight * ingredientWeightMultiplier);
        this.calories = ingredient.getCaloriesPer100g() * weight / 100;
        this.protein = ingredient.getProteinPer100g() * weight / 100;
        this.fats = ingredient.getFatsPer100g() * weight / 100;
        this.carbs = ingredient.getCarbsPer100g() * weight / 100;
    }

    public FinalIngredient(FinalIngredientResponseForm finalIngredientResponseForm, IngredientRepository ingredientRepository) {
        this.ingredient = ingredientRepository.findById(finalIngredientResponseForm.getIdNewIngredient()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        this.weight = finalIngredientResponseForm.getWeight();
        this.calories = ingredient.getCaloriesPer100g() * weight / 100;
        this.protein = ingredient.getProteinPer100g() * weight / 100;
        this.fats = ingredient.getFatsPer100g() * weight / 100;
        this.carbs = ingredient.getCarbsPer100g() * weight / 100;
    }

    public void modifyFinalIngredient(FinalIngredientResponseForm finalIngredientResponseForm) {
        this.weight = finalIngredientResponseForm.getWeight();
        this.calories = ingredient.getCaloriesPer100g() * weight / 100;
        this.protein = ingredient.getProteinPer100g() * weight / 100;
        this.fats = ingredient.getFatsPer100g() * weight / 100;
        this.carbs = ingredient.getCarbsPer100g() * weight / 100;
    }
}
