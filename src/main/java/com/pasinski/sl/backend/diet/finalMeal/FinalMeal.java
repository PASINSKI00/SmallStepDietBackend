package com.pasinski.sl.backend.diet.finalMeal;

import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.forms.FinalIngredientResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalMealResponseForm;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        this.finalIngredients = meal.getIngredients().stream().map(mealIngredient -> new FinalIngredient(mealIngredient, ingredientWeightMultiplier)).collect(Collectors.toList());
        calculateEnergeticValues();
    }

    public void modifyPercentOfDay(Integer percentOfDay, Integer caloriesGoal) {
        this.percentOfDay = percentOfDay;
        this.caloriesGoal = caloriesGoal;
        this.ingredientWeightMultiplier = (float) caloriesGoal / meal.getInitialCalories();

        this.finalIngredients.clear();
        meal.getIngredients().stream()
                .map(mealIngredient -> new FinalIngredient(mealIngredient, ingredientWeightMultiplier))
                .forEach(finalIngredient -> this.finalIngredients.add(finalIngredient));
        calculateEnergeticValues();
    }

    public void modifyFinalMeal(FinalMealResponseForm finalMealResponseForm, IngredientRepository ingredientRepository) {
//        check for deleted
        List<String> ingredientsNamesToRemove = finalMealResponseForm.getFinalIngredients().stream()
                .filter(finalIngredientResponseForm -> finalIngredientResponseForm.getRemove() != null && finalIngredientResponseForm.getRemove())
                .map(FinalIngredientResponseForm::getName)
                .toList();
        this.finalIngredients.removeIf(finalIngredient -> ingredientsNamesToRemove.contains(finalIngredient.getIngredient().getName()));


//        modify weights
        finalMealResponseForm.getFinalIngredients().forEach((finalIngredientResponseForm) -> {
            this.finalIngredients.stream()
                    .filter(finalIngredient -> finalIngredient.getIngredient().getName().equals(finalIngredientResponseForm.getName()))
                    .findFirst()
                    .ifPresent(finalIngredient -> {
                        finalIngredient.modifyFinalIngredient(finalIngredientResponseForm);
//                        fin
                    });
        });

//        add new if present
        finalMealResponseForm.getFinalIngredients().stream()
                .filter(finalIngredientResponseForm -> finalIngredientResponseForm.getIdNewIngredient() != null)
                .forEach(finalIngredientResponseForm -> {
                    this.finalIngredients.add(new FinalIngredient(finalIngredientResponseForm, ingredientRepository));
                });

        calculateEnergeticValues();
    }

    private void calculateEnergeticValues() {
        this.calories = this.finalIngredients.stream().mapToInt(FinalIngredient::getCalories).sum();
        this.protein = this.finalIngredients.stream().mapToInt(FinalIngredient::getProtein).sum();
        this.fats = this.finalIngredients.stream().mapToInt(FinalIngredient::getFats).sum();
        this.carbs = this.finalIngredients.stream().mapToInt(FinalIngredient::getCarbs).sum();
    }
}
