package com.pasinski.sl.backend.meal.ingredient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingredient", nullable = false)
    private Long idIngredient;

    @NotNull
    private String name;
    @NotNull
    private Integer caloriesPer100g;
    @NotNull
    private Integer proteinPer100g;
    @NotNull
    private Integer fatsPer100g;
    @NotNull
    private Integer carbsPer100g;

    public Ingredient(Long idIngredient, String name) {
        this.idIngredient = idIngredient;
        this.name = name;
    }
}
