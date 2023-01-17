package com.pasinski.sl.backend.diet.finalMeal;

import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.meal.Meal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Setter
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
    private Integer initialCalories;
}
