package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FinalMealResponseForm {
    private Long idFinalMeal;
    private String name;
    private List<FinalIngredientResponseForm> finalIngredients;
    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;
    private Integer percentOfDay;

    private String imageUrl;

    public FinalMealResponseForm(FinalMeal finalMeal) {
        this.idFinalMeal = finalMeal.getIdFinalMeal();
        this.name = finalMeal.getMeal().getName();
        this.finalIngredients = new ArrayList<>();
        this.calories = finalMeal.getCalories();
        this.protein = finalMeal.getProtein();
        this.fats = finalMeal.getFats();
        this.carbs = finalMeal.getCarbs();
        this.percentOfDay = finalMeal.getPercentOfDay();

        finalMeal.getFinalIngredients().forEach(finalIngredient -> this.finalIngredients.add(new FinalIngredientResponseForm(finalIngredient)));
    }
}
