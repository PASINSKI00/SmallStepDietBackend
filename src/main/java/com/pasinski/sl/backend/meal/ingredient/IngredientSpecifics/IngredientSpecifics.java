package com.pasinski.sl.backend.meal.ingredient.IngredientSpecifics;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class IngredientSpecifics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingredient_specifics", nullable = false)
    private Long idMealSpecifics;
    private Integer weight;
    private Integer initialRatioInMeal;
}
