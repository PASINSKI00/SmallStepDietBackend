package com.pasinski.sl.backend.diet.FinalIngredient;

import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FinalIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_final_ingredient", nullable = false)
    private Long idFinalIngredient;

    @ManyToOne
    private Ingredient ingredient;

    private Integer weight;
    private Integer initialWeight;
    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;
}
