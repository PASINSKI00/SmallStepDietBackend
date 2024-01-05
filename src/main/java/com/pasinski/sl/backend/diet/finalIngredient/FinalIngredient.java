package com.pasinski.sl.backend.diet.finalIngredient;

import com.pasinski.sl.backend.diet.forms.request.FinalIngredientModifyRequestForm;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.meal.mealIngredient.MealIngredient;
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

    public FinalIngredient(MealIngredient mealIngredient, Float ingredientWeightMultiplier) {
        this.ingredient = mealIngredient.getIngredient();
        this.initialWeight = mealIngredient.getInitialWeight();

        this.weight = (int) (mealIngredient.getInitialWeight() * ingredientWeightMultiplier);
        this.calories = (int) (mealIngredient.getIngredient().getCaloriesPer100g() * weight / 100);
        this.protein = (int) (mealIngredient.getIngredient().getProteinPer100g() * weight / 100);
        this.fats = (int) (mealIngredient.getIngredient().getFatsPer100g() * weight / 100);
        this.carbs = (int) (mealIngredient.getIngredient().getCarbsPer100g() * weight / 100);
    }

    public FinalIngredient(FinalIngredientModifyRequestForm finalIngredientResponseForm, IngredientRepository ingredientRepository) {
        this.ingredient = ingredientRepository.findById(finalIngredientResponseForm.idNewIngredient()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        this.weight = finalIngredientResponseForm.weight();
        this.calories = (int) (ingredient.getCaloriesPer100g() * weight / 100);
        this.protein = (int) (ingredient.getProteinPer100g() * weight / 100);
        this.fats = (int) (ingredient.getFatsPer100g() * weight / 100);
        this.carbs = (int) (ingredient.getCarbsPer100g() * weight / 100);
    }

    public void modifyFinalIngredient(FinalIngredientModifyRequestForm modifiedIngredient) {
        this.weight = modifiedIngredient.weight();
        this.calories = (int) (ingredient.getCaloriesPer100g() * weight / 100);
        this.protein = (int) (ingredient.getProteinPer100g() * weight / 100);
        this.fats = (int) (ingredient.getFatsPer100g() * weight / 100);
        this.carbs = (int) (ingredient.getCarbsPer100g() * weight / 100);
    }
}
