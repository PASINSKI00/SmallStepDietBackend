package com.pasinski.sl.backend.meal.mealIngredientSpecifics;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class MealIngredientSpecifics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meal_ingredient_specifics", nullable = false)
    private Long idMealSpecifics;
    private Integer initialWeight;
    private Integer initialRatioInMeal;
}
