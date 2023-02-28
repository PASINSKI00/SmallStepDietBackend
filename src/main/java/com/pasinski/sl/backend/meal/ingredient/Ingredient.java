package com.pasinski.sl.backend.meal.ingredient;

import com.pasinski.sl.backend.meal.forms.IngredientForm;
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
    private Float caloriesPer100g;
    @NotNull
    private Float proteinPer100g;
    @NotNull
    private Float fatsPer100g;
    @NotNull
    private Float carbsPer100g;

    public Ingredient(Long idIngredient, String name) {
        this.idIngredient = idIngredient;
        this.name = name;
    }

    public Ingredient(IngredientForm ingredientForm) {
        this.name = ingredientForm.getName();
        this.caloriesPer100g = ingredientForm.getCalories();
        this.proteinPer100g = ingredientForm.getProtein();
        this.fatsPer100g = ingredientForm.getFats();
        this.carbsPer100g = ingredientForm.getCarbs();
    }
}
