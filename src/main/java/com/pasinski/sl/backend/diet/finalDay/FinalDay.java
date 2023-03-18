package com.pasinski.sl.backend.diet.finalDay;

import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.forms.FinalDayResponseForm;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FinalDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_final_day", nullable = false)
    private Long idFinalDay;

    @OneToMany(orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private List<FinalMeal> finalMeals;

    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;

    public FinalDay(List<Meal> meals, Integer caloriesGoal) {
        this.finalMeals = new ArrayList<>();

        List<Integer> mealRatios = calculateMealRatiosForFinalMeals(meals.size());
        List<Integer> caloriesGoals = calculateCaloriesGoalsForFinalMeals(caloriesGoal, mealRatios);
        for (int i = 0; i < meals.size(); i++)
            this.finalMeals.add(new FinalMeal(meals.get(i), caloriesGoals.get(i), mealRatios.get(i)));
        this.calories = finalMeals.stream().mapToInt(FinalMeal::getCalories).sum();
        this.protein = finalMeals.stream().mapToInt(FinalMeal::getProtein).sum();
        this.fats = finalMeals.stream().mapToInt(FinalMeal::getFats).sum();
        this.carbs = finalMeals.stream().mapToInt(FinalMeal::getCarbs).sum();
    }

    private List<Integer> calculateMealRatiosForFinalMeals(Integer size) {
        List<Integer> percents = new ArrayList<>();
        for (int i = 0; i < size; i++)
            percents.add(100 / size);

        if (percents.stream().mapToInt(Integer::intValue).sum() != 100)
            percents.set(percents.size() - 1, percents.get(percents.size() - 1) + (100 - percents.stream().mapToInt(Integer::intValue).sum()));

        return percents;
    }

    private List<Integer> calculateCaloriesGoalsForFinalMeals(Integer caloriesGoal, List<Integer> mealRatios) {
        List<Integer> caloriesGoals = new ArrayList<>();
        mealRatios.forEach(percent -> {
            caloriesGoals.add((caloriesGoal * percent) / 100);
        });

        if (caloriesGoals.stream().mapToInt(Integer::intValue).sum() != caloriesGoal)
            caloriesGoals.set(caloriesGoals.size() - 1, caloriesGoals.get(caloriesGoals.size() - 1) + (caloriesGoal - caloriesGoals.stream().mapToInt(Integer::intValue).sum()));

        return caloriesGoals;
    }

    public void modifyFinalDay(FinalDayResponseForm finalDayResponseForm, IngredientRepository ingredientRepository, Integer caloriesGoal) {
        if (finalDayResponseForm.getFinalMeals().get(0).getPercentOfDay() != null) {
            List<Integer> percents = new ArrayList<>();
            finalDayResponseForm.getFinalMeals().forEach(finalMealResponseForm1 -> {
                percents.add(finalMealResponseForm1.getPercentOfDay());
            });

            if (percents.stream().mapToInt(Integer::intValue).sum() != 100)
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Sum of percents of meals must be equal 100");

            List<Integer> caloriesGoals = calculateCaloriesGoalsForFinalMeals(caloriesGoal, percents);

            for (int i = 0; i < finalMeals.size(); i++)
                finalMeals.get(i).modifyPercentOfDay(percents.get(i), caloriesGoals.get(i));
        }

        finalDayResponseForm.getFinalMeals().forEach(finalMealResponseForm -> {
            if (finalMealResponseForm.getFinalIngredients() != null)
                finalMeals.stream()
                        .filter(finalMeal -> finalMeal.getIdFinalMeal().equals(finalMealResponseForm.getIdFinalMeal())).findFirst()
                        .get().modifyFinalMeal(finalMealResponseForm, ingredientRepository);

            this.calories = finalMeals.stream().mapToInt(FinalMeal::getCalories).sum();
            this.protein = finalMeals.stream().mapToInt(FinalMeal::getProtein).sum();
            this.fats = finalMeals.stream().mapToInt(FinalMeal::getFats).sum();
            this.carbs = finalMeals.stream().mapToInt(FinalMeal::getCarbs).sum();
        });
    }
}
