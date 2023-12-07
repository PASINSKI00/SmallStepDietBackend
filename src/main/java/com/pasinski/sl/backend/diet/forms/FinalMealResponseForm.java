package com.pasinski.sl.backend.diet.forms;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FinalMealResponseForm {
    private Long idFinalMeal;
    private String name;
    private List<FinalIngredientResponseForm> finalIngredients;
    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;
    private Integer percentOfDay;
    private String recipe;
    private String imageUrl;

    public FinalMealResponseForm(FinalMeal finalMeal, S3Service s3Service) {
        this.idFinalMeal = finalMeal.getIdFinalMeal();
        this.name = finalMeal.getMeal().getName();
        this.finalIngredients = new ArrayList<>();
        this.calories = finalMeal.getCalories();
        this.protein = finalMeal.getProtein();
        this.fats = finalMeal.getFats();
        this.carbs = finalMeal.getCarbs();
        this.percentOfDay = finalMeal.getPercentOfDay();
        String imageName = finalMeal.getMeal().isImageSet() ?
                ApplicationConstants.getMealImageName(finalMeal.getMeal().getIdMeal()) :
                ApplicationConstants.DEFAULT_MEAL_IMAGE_NAME;
        this.imageUrl = s3Service.getFileUrl(imageName, FileType.MEAL_IMAGE);
        this.recipe = finalMeal.getMeal().getMealExtention().getRecipe();
        finalMeal.getFinalIngredients().forEach(finalIngredient -> this.finalIngredients.add(new FinalIngredientResponseForm(finalIngredient)));
    }
}
