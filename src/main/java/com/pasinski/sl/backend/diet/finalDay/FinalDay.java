package com.pasinski.sl.backend.diet.finalDay;

import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.forms.request.FinalDayModifyRequestForm;
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

        this.calculateMacro();
    }

    public void resetDay(Integer caloriesGoal) {
        List<Integer> mealRatios = calculateMealRatiosForFinalMeals(this.finalMeals.size());
        List<Integer> caloriesGoals = calculateCaloriesGoalsForFinalMeals(caloriesGoal, mealRatios);

        for (int i = 0; i < this.finalMeals.size(); i++)
            this.finalMeals.get(i).resetMeal(caloriesGoals.get(i), mealRatios.get(i));

        this.calculateMacro();
    }

    public void modifyFinalDay(FinalDayModifyRequestForm modifiedDay, IngredientRepository ingredientRepository, Integer caloriesGoal) {
        if (modifiedDay.finalMeals().get(0).percentOfDay() != null) {
            List<Integer> percents = new ArrayList<>();
            modifiedDay.finalMeals().forEach(modifiedMeal -> {
                percents.add(modifiedMeal.percentOfDay());
            });

            if (percents.stream().mapToInt(Integer::intValue).sum() != 100)
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Sum of percents of meals must be equal 100");

            List<Integer> caloriesGoals = calculateCaloriesGoalsForFinalMeals(caloriesGoal, percents);

            for (int i = 0; i < finalMeals.size(); i++) {
                finalMeals.get(i).modifyPercentOfDay(percents.get(i), caloriesGoals.get(i));
            }
        }

        modifiedDay.finalMeals().forEach(modifiedMeal -> {
            if (modifiedMeal.finalIngredients() != null)
                finalMeals.stream()
                        .filter(finalMeal -> finalMeal.getIdFinalMeal().equals(modifiedMeal.idFinalMeal()))
                        .findFirst().get().modifyIngredients(modifiedMeal, ingredientRepository);

            this.calculateMacro();
        });
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

    private void calculateMacro(){
        this.calories = finalMeals.stream().mapToInt(FinalMeal::getCalories).sum();
        this.protein = finalMeals.stream().mapToInt(FinalMeal::getProtein).sum();
        this.fats = finalMeals.stream().mapToInt(FinalMeal::getFats).sum();
        this.carbs = finalMeals.stream().mapToInt(FinalMeal::getCarbs).sum();
    }
}
