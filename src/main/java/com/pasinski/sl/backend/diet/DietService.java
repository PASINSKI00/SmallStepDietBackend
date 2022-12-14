package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.FinalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.FinalIngredient.FinalIngredientRepository;
import com.pasinski.sl.backend.diet.PDFGenerator.PDFGeneratorService;
import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.diet.finalDay.FinalDayRepository;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.finalMeal.FinalMealRepository;
import com.pasinski.sl.backend.diet.forms.DietResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalDayResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalIngredientResponseForm;
import com.pasinski.sl.backend.diet.forms.FinalMealResponseForm;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.security.UserSecurityService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
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
    private final PDFGeneratorService pdfGeneratorService;

    public DietResponseForm getDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        DietResponseForm dietResponseForm = new DietResponseForm();

        if(!Objects.equals(diet.getAppUser().getIdUser(), this.userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        dietResponseForm.setIdDiet(diet.getIdDiet());
        dietResponseForm.setFinalDays(new ArrayList<>());

        diet.getFinalDays().forEach(finalDay -> {
            FinalDayResponseForm finalDayResponseForm = new FinalDayResponseForm();
            finalDayResponseForm.setIdFinalDay(finalDay.getIdFinalDay());
            finalDayResponseForm.setFinalMeals(new ArrayList<>());
            finalDayResponseForm.setCalories(finalDay.getCalories());
            finalDayResponseForm.setCarbs(finalDay.getCarbs());
            finalDayResponseForm.setProtein(finalDay.getProtein());
            finalDayResponseForm.setFats(finalDay.getFats());

            finalDay.getFinalMeals().forEach(finalMeal -> {
                FinalMealResponseForm finalMealResponseForm = new FinalMealResponseForm();
                finalMealResponseForm.setIdFinalMeal(finalMeal.getIdFinalMeal());
                finalMealResponseForm.setName(finalMeal.getMeal().getName());
                finalMealResponseForm.setFinalIngredients(new ArrayList<>());
                finalMealResponseForm.setCalories(finalMeal.getCalories());
                finalMealResponseForm.setProtein(finalMeal.getProtein());
                finalMealResponseForm.setFats(finalMeal.getFats());
                finalMealResponseForm.setCarbs(finalMeal.getCarbs());
                finalMealResponseForm.setPercentOfDay(finalMeal.getPercentOfDay());

                finalMeal.getFinalIngredients().forEach(finalIngredient -> {
                    FinalIngredientResponseForm finalIngredientResponseForm = new FinalIngredientResponseForm();
                    finalIngredientResponseForm.setIdFinalIngredient(finalIngredient.getIdFinalIngredient());
                    finalIngredientResponseForm.setName(finalIngredient.getIngredient().getName());
                    finalIngredientResponseForm.setWeight(finalIngredient.getWeight());
                    finalMealResponseForm.getFinalIngredients().add(finalIngredientResponseForm);
                });
                finalDayResponseForm.getFinalMeals().add(finalMealResponseForm);
            });
            dietResponseForm.getFinalDays().add(finalDayResponseForm);
        });

       return dietResponseForm;
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

    public String generateDietPDF(Long idDiet) throws FileNotFoundException {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return this.pdfGeneratorService.generateDietPDF(diet);
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
