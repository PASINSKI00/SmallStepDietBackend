package com.pasinski.sl.backend.diet.forms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.criteria.CriteriaBuilder;
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
}
