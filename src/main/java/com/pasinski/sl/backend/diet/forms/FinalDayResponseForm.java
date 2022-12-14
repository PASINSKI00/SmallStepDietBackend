package com.pasinski.sl.backend.diet.forms;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FinalDayResponseForm {
    private Long idFinalDay;
    private List<FinalMealResponseForm> finalMeals;
    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;
}
