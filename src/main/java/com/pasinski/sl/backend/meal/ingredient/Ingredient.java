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

    private Float fiberPer100g;
    private Float calciumPer100gInMg;
    private Float ironPer100gInMg;
    private Float magnesiumPer100gInMg;
    private Float phosphorusPer100gInMg;
    private Float potassiumPer100gInMg;
    private Float sodiumPer100gInMg;
    private Float zincPer100gInMg;
    private Float copperPer100gInMg;
    private Float manganesePer100gInMg;
    private Float seleniumPer100gInUg;
    private Float vitCPer100gInMg;
    private Float thiaminPer100gInMg;
    private Float riboflavinPer100gInMg;
    private Float niacinPer100gInMg;
    private Float pantoAcidPer100gInMg;
    private Float vitB6Per100gInMg;
    private Float folatePer100gInUg;
    private Float cholinePer100gInMg;
    private Float vitB12Per100gInMg;
    private Float vitAPer100gInRAE;
    private Float retinolPer100gInUg;
    private Float vitEPer100gInMg;
    private Float vitDPer100gInUg;
    private Float vitKPer100gInUg;
    private Float cholesterolPer100gInMg;

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
