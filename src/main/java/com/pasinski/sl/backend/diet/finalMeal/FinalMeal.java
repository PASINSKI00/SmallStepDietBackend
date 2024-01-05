package com.pasinski.sl.backend.diet.finalMeal;

import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.forms.request.FinalIngredientModifyRequestForm;
import com.pasinski.sl.backend.diet.forms.request.FinalMealModifyRequestForm;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FinalMeal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_final_meal", nullable = false)
    private Long idFinalMeal;
    @ManyToOne
    private Meal meal;
    @OneToMany(orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private List<FinalIngredient> finalIngredients;

    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;
    private Float ingredientWeightMultiplier;
    private Integer percentOfDay;
    private Integer caloriesGoal;

    public FinalMeal(Meal meal, Integer calorieGoal, Integer percentOfDay) {
        this.meal = meal;
        this.finalIngredients = new ArrayList<>();
        this.caloriesGoal = calorieGoal;
        this.percentOfDay = percentOfDay;
        this.ingredientWeightMultiplier = (float) caloriesGoal / meal.getInitialCalories();

        this.meal.setTimesUsed(this.meal.getTimesUsed() + 1);
        this.finalIngredients = meal.getIngredients().stream()
                .map(mealIngredient -> new FinalIngredient(mealIngredient, ingredientWeightMultiplier)).toList();
        this.calculateEnergeticValues();
    }

    public void resetMeal(Integer calorieGoal, Integer percentOfDay) {
        this.caloriesGoal = calorieGoal;
        this.percentOfDay = percentOfDay;
        this.ingredientWeightMultiplier = (float) caloriesGoal / meal.getInitialCalories();

        this.finalIngredients.clear();
        meal.getIngredients().forEach(mealIngredient ->
                this.finalIngredients.add(new FinalIngredient(mealIngredient, ingredientWeightMultiplier)));
        this.calculateEnergeticValues();
    }

    public void modifyPercentOfDay(Integer percentOfDay, Integer caloriesGoal) {
        this.percentOfDay = percentOfDay;
        this.caloriesGoal = caloriesGoal;
        this.ingredientWeightMultiplier = (float) caloriesGoal / meal.getInitialCalories();

        List<Ingredient> ingredientsBefore = new ArrayList<>(this.finalIngredients.stream().map(FinalIngredient::getIngredient).toList());
        this.finalIngredients.clear();
        meal.getIngredients().stream()
                .map(mealIngredient -> new FinalIngredient(mealIngredient, ingredientWeightMultiplier))
                .forEach(finalIngredient -> this.finalIngredients.add(finalIngredient));

        this.finalIngredients.removeIf(finalIngredient -> !ingredientsBefore.contains(finalIngredient.getIngredient()));
        this.calculateEnergeticValues();
    }

    public void modifyIngredients(FinalMealModifyRequestForm modifiedMeal, IngredientRepository ingredientRepository) {
//        check for deleted
        List<String> ingredientsNamesToRemove = modifiedMeal.finalIngredients().stream()
                .filter(modifiedIngredient -> modifiedIngredient.remove() != null && modifiedIngredient.remove())
                .map(FinalIngredientModifyRequestForm::name)
                .toList();
        this.finalIngredients
                .removeIf(finalIngredient -> ingredientsNamesToRemove.contains(finalIngredient.getIngredient().getName()));


//        modify weights
        modifiedMeal.finalIngredients().forEach((finalIngredientResponseForm) -> {
            this.finalIngredients.stream()
                    .filter(finalIngredient -> finalIngredient.getIngredient().getName().equals(finalIngredientResponseForm.name()))
                    .findFirst()
                    .ifPresent(finalIngredient -> {
                        finalIngredient.modifyFinalIngredient(finalIngredientResponseForm);
//                        fin
                    });
        });

//        add new if present
        modifiedMeal.finalIngredients().stream()
                .filter(ingredientRequest -> ingredientRequest.idNewIngredient() != null)
                .forEach(ingredientRequest -> {
                    this.finalIngredients.add(new FinalIngredient(ingredientRequest, ingredientRepository));
                });

        this.calculateEnergeticValues();
    }

    private void calculateEnergeticValues() {
        this.calories = this.finalIngredients.stream().mapToInt(FinalIngredient::getCalories).sum();
        this.protein = this.finalIngredients.stream().mapToInt(FinalIngredient::getProtein).sum();
        this.fats = this.finalIngredients.stream().mapToInt(FinalIngredient::getFats).sum();
        this.carbs = this.finalIngredients.stream().mapToInt(FinalIngredient::getCarbs).sum();
    }
}
