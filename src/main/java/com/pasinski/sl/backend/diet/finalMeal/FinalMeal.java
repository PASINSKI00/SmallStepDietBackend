package com.pasinski.sl.backend.diet.finalMeal;

import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.meal.Meal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.CascadeType;

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
    @OneToMany
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
        meal.getIngredients().forEach((ingredient, specifics) -> {
            this.finalIngredients.add(new FinalIngredient(ingredient, specifics, ingredientWeightMultiplier));
        });
        this.calories = this.finalIngredients.stream().mapToInt(FinalIngredient::getCalories).sum();
        this.protein = this.finalIngredients.stream().mapToInt(FinalIngredient::getProtein).sum();
        this.fats = this.finalIngredients.stream().mapToInt(FinalIngredient::getFats).sum();
        this.carbs = this.finalIngredients.stream().mapToInt(FinalIngredient::getCarbs).sum();
    }
}
