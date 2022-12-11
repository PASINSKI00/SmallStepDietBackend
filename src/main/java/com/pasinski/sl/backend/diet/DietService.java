package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.FinalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.FinalIngredient.FinalIngredientRepository;
import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.diet.finalDay.FinalDayRepository;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.finalMeal.FinalMealRepository;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.security.UserSecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Service
@AllArgsConstructor
public class DietService {
    private final DietRepository dietRepository;
    private final MealRepository mealRepository;
    private final FinalDayRepository finalDayRepository;
    private final FinalMealRepository finalMealRepository;
    private final FinalIngredientRepository finalIngredientRepository;
    private final UserSecurityService userSecurityService;

    public Diet getDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(!Objects.equals(diet.getAppUser().getIdUser(), this.userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        diet.setAppUser(null);
        diet.getFinalDays().forEach(finalDay -> {
            finalDay.getFinalMeals().forEach(finalMeal -> {
                finalMeal.setMeal(null);
                finalMeal.getFinalIngredients().forEach(finalIngredient -> finalIngredient.setIngredient(null));
            });
        });

       return diet;
    }

    public Long addDiet(Long[][] days) {
        List<List<Long>> daysForm = new ArrayList<>();
        Arrays.stream(days).forEach(day -> daysForm.add(Arrays.asList(day)));

        List<List<Meal>> daysMeal = new ArrayList<>();

//        Get Meals from database
        daysForm.forEach(day -> {
            daysMeal.add(new ArrayList<>());
            day.forEach(idMeal -> {
                Meal meal = this.mealRepository.findById(idMeal).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
                daysMeal.get(daysMeal.size() - 1).add(meal);
            });
        });

//        Create FinalMeals inside FinalDays
        List<FinalDay> finalDays = new ArrayList<>();
        daysMeal.forEach(day -> {
            finalDays.add(new FinalDay());
            finalDays.get(finalDays.size() - 1).setFinalMeals(new ArrayList<>());
            this.finalDayRepository.save(finalDays.get(finalDays.size() - 1));
            day.forEach(meal -> {
                FinalMeal finalMeal = new FinalMeal();
                finalMeal.setMeal(meal);
                this.finalMealRepository.save(finalMeal);
                finalDays.get(finalDays.size() - 1).getFinalMeals().add(finalMeal);
            });
        });

//        TODO: Get caloriesGoal from user
        Integer calories = 3000;

//        create final meals for each day
        finalDays.forEach(finalDay -> {
            List<Integer> percentsOfMeals = calculatePercentagesOfMealsForDay(finalDay.getFinalMeals().size());
            List<Integer> caloriesGoals = calculateCaloriesGoalsForDay(calories, percentsOfMeals);
            finalDay.getFinalMeals().forEach(finalMeal -> {
                finalMeal.setPercentOfDay(percentsOfMeals.get(finalDay.getFinalMeals().indexOf(finalMeal)));
                finalMeal.setCaloriesGoal(caloriesGoals.get(finalDay.getFinalMeals().indexOf(finalMeal)));
                finalMeal.setInitialCalories(getInitialCaloriesOfMeal(finalMeal.getMeal()));
                finalMeal.setIngredientWeightMultiplier(setIngredientsWeightMultiplier(finalMeal.getInitialCalories(), finalMeal.getCaloriesGoal(), finalMeal.getMeal()));
                finalMeal.setFinalIngredients(getFinalIngredientsOfMeal(finalMeal.getMeal()));
            });
        });

        finalDays.forEach(finalDay -> {
            finalDay.getFinalMeals().forEach(finalMeal -> {
                setFinalIngredientsValues(finalMeal.getFinalIngredients(), finalMeal.getIngredientWeightMultiplier());
                setFinalMealValues(finalMeal);
                this.finalIngredientRepository.saveAll(finalMeal.getFinalIngredients());
                this.finalMealRepository.save(finalMeal);
            });
        });

        //TODO: Set values of finalDay
        finalDays.forEach(finalDay -> {
            setFinalDayValues(finalDay);
        });


        Diet diet = new Diet();
        diet.setAppUser(this.userSecurityService.getLoggedUser());
        diet.setFinalDays(finalDays);

        this.dietRepository.save(diet);
        return diet.getIdDiet();
    }

    private List<Integer> calculatePercentagesOfMealsForDay(int size) {
        List<Integer> percents = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            percents.add(100 / size);
        }

        if(percents.stream().mapToInt(Integer::intValue).sum() != 100) {
            percents.set(percents.size() - 1, percents.get(percents.size() - 1) + (100 - percents.stream().mapToInt(Integer::intValue).sum()));
        }

        return percents;
    }

    private List<Integer> calculateCaloriesGoalsForDay(Integer calories, List<Integer> percentsOfMeals) {
        List<Integer> caloriesGoals = new ArrayList<>();
        percentsOfMeals.forEach(percent -> {
            caloriesGoals.add((calories * percent) / 100);
        });

        if(caloriesGoals.stream().mapToInt(Integer::intValue).sum() != calories) {
            caloriesGoals.set(caloriesGoals.size() - 1, caloriesGoals.get(caloriesGoals.size() - 1) + (calories - caloriesGoals.stream().mapToInt(Integer::intValue).sum()));
        }

        return caloriesGoals;
    }

    private Integer getInitialCaloriesOfMeal(Meal meal) {
        final Integer[] calories = {0};

        meal.getIngredients().forEach((key, value) -> calories[0] += key.getCaloriesPer100g() * value.getInitialWeight() / 100);

        return calories[0];
    }

    private List<FinalIngredient> getFinalIngredientsOfMeal(Meal meal) {
        List<FinalIngredient> finalIngredients = new ArrayList<>();
        meal.getIngredients().forEach((ingredient, specifics) -> {
            FinalIngredient finalIngredient = new FinalIngredient();
            finalIngredient.setInitialWeight(specifics.getInitialWeight());
            finalIngredient.setIngredient(ingredient);
            finalIngredients.add(finalIngredient);
        });

        return finalIngredients;
    }

    private Float setIngredientsWeightMultiplier(Integer initialCalories, Integer caloriesGoal, Meal meal) {
        return (float) caloriesGoal / initialCalories;
    }

    private void setFinalIngredientsValues(List<FinalIngredient> finalIngredients, Float ingredientWeightMultiplier) {
        finalIngredients.forEach(finalIngredient -> {
            // TODO verify if this is correct
            finalIngredient.setWeight((int)     (finalIngredient.getInitialWeight() * ingredientWeightMultiplier));
            finalIngredient.setProtein((int)    (finalIngredient.getIngredient().getProteinPer100g() * finalIngredient.getWeight() / 100));
            finalIngredient.setFats((int)       (finalIngredient.getIngredient().getFatsPer100g() * finalIngredient.getWeight() / 100));
            finalIngredient.setCarbs((int)      (finalIngredient.getIngredient().getCarbsPer100g() * finalIngredient.getWeight() / 100));
            finalIngredient.setCalories((int)   (finalIngredient.getIngredient().getCaloriesPer100g() * finalIngredient.getWeight() / 100));
        });
    }

    private void setFinalMealValues(FinalMeal finalMeal) {
        finalMeal.setProtein(finalMeal.getFinalIngredients().stream().mapToInt(FinalIngredient::getProtein).sum());
        finalMeal.setFats(finalMeal.getFinalIngredients().stream().mapToInt(FinalIngredient::getFats).sum());
        finalMeal.setCarbs(finalMeal.getFinalIngredients().stream().mapToInt(FinalIngredient::getCarbs).sum());
        finalMeal.setCalories(finalMeal.getFinalIngredients().stream().mapToInt(FinalIngredient::getCalories).sum());
    }

    private void setFinalDayValues(FinalDay finalDay) {
        finalDay.setProtein(finalDay.getFinalMeals().stream().mapToInt(FinalMeal::getProtein).sum());
        finalDay.setFats(finalDay.getFinalMeals().stream().mapToInt(FinalMeal::getFats).sum());
        finalDay.setCarbs(finalDay.getFinalMeals().stream().mapToInt(FinalMeal::getCarbs).sum());
        finalDay.setCalories(finalDay.getFinalMeals().stream().mapToInt(FinalMeal::getCalories).sum());
    }
}
