package com.pasinski.sl.backend.meal.mealIngredient;

import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MealIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meal_ingredient", nullable = false)
    private Long idMealIngredient;

    @ManyToOne
    @JoinColumn(name = "ingredient_id_ingredient")
    private Ingredient ingredient;
    private Integer initialWeight;

    public MealIngredient(Ingredient ingredient, Integer amount) {
        this.ingredient = ingredient;
        this.initialWeight = amount;
    }
}
