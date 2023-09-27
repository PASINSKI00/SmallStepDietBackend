package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.file.S3Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FinalDayResponseForm {
    private Long idFinalDay;
    private List<FinalMealResponseForm> finalMeals;
    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;

    public FinalDayResponseForm(FinalDay finalDay, S3Service s3Service) {
        this.idFinalDay = finalDay.getIdFinalDay();
        this.finalMeals = new ArrayList<>();
        this.calories = finalDay.getCalories();
        this.protein = finalDay.getProtein();
        this.carbs = finalDay.getCarbs();
        this.fats = finalDay.getFats();

        finalDay.getFinalMeals().forEach(finalMeal -> this.finalMeals.add(new FinalMealResponseForm(finalMeal, s3Service)));
        this.finalMeals.sort(Comparator.comparing(FinalMealResponseForm::getIdFinalMeal));
    }
}
