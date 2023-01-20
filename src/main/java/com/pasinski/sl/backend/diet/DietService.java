package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredientRepository;
import com.pasinski.sl.backend.diet.PDFGenerator.PDFGeneratorService;
import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.diet.finalDay.FinalDayRepository;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.finalMeal.FinalMealRepository;
import com.pasinski.sl.backend.diet.forms.*;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.review.Review;
import com.pasinski.sl.backend.security.UserSecurityService;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.meal.category.Category;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.FileSystems;
import java.util.*;
import java.util.stream.Collectors;

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
                meal.setTimesUsed(meal.getTimesUsed() + 1);
                this.finalMealRepository.save(finalMeal);
                finalDays.get(finalDays.size() - 1).getFinalMeals().add(finalMeal);
            });
        });

        if(userSecurityService.getLoggedUser().getBodyInfo() == null)
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY);

        Integer calories = userSecurityService.getLoggedUser().getBodyInfo().getCaloriesGoal();

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

        finalDays.forEach(this::setFinalDayValues);


        Diet diet = new Diet();
        diet.setAppUser(this.userSecurityService.getLoggedUser());
        diet.setFinalDays(finalDays);

        this.dietRepository.save(diet);
        return diet.getIdDiet();
    }

    public void updateDiet(Long idDiet, Long[][] days) {
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

        //Delete old finalMeals
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        diet.getFinalDays().forEach(finalDay -> {
            finalDay.getFinalMeals().forEach(finalMeal -> {
                this.finalIngredientRepository.deleteAll(finalMeal.getFinalIngredients());
                this.finalMealRepository.delete(finalMeal);
            });
            this.finalDayRepository.delete(finalDay);
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
                meal.setTimesUsed(meal.getTimesUsed() + 1);
                this.finalMealRepository.save(finalMeal);
                finalDays.get(finalDays.size() - 1).getFinalMeals().add(finalMeal);
            });
        });

        if(userSecurityService.getLoggedUser().getBodyInfo() == null)
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY);

        Integer calories = userSecurityService.getLoggedUser().getBodyInfo().getCaloriesGoal();

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

        finalDays.forEach(this::setFinalDayValues);

        diet.setFinalDays(finalDays);

        this.dietRepository.save(diet);
    }

    public String generateDietPDF(Long idDiet) throws FileNotFoundException {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return this.pdfGeneratorService.generateDietPDF(diet);
    }

    public InputStreamResource getDietPdf(String fileName) throws FileNotFoundException {
        File file = new File(ApplicationConstants.PATH_TO_PDF_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public List<Grocery> getGroceries(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if(!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        List<Grocery> groceries = new ArrayList<>();
        diet.getFinalDays().forEach(finalDay -> {
            finalDay.getFinalMeals().forEach(finalMeal -> {
                finalMeal.getFinalIngredients().forEach(finalIngredient -> {
                    Grocery grocery = new Grocery();
                    grocery.setName(finalIngredient.getIngredient().getName());
                    grocery.setWeight(finalIngredient.getWeight());
                    groceries.add(grocery);
                });
            });
        });

        List<Grocery> groceriesSummed = new ArrayList<>();
        groceries.forEach(grocery -> {
            if(groceriesSummed.stream().anyMatch(grocery1 -> Objects.equals(grocery1.getName(), grocery.getName()))) {
                groceriesSummed.stream().filter(grocery1 -> Objects.equals(grocery1.getName(), grocery.getName())).forEach(grocery1 -> {
                    grocery1.setWeight(grocery1.getWeight() + grocery.getWeight());
                });
            } else {
                groceriesSummed.add(grocery);
            }
        });

        return groceriesSummed;
    }

    public String generateGroceriesPDF(Long idDiet) throws FileNotFoundException {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        List<Grocery> groceries = getGroceries(idDiet);

        if(!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return this.pdfGeneratorService.generateGroceriesPDF(groceries);
    }

    public InputStreamResource getGroceriesPdf(String fileName) throws FileNotFoundException {
        File file = new File(ApplicationConstants.PATH_TO_PDF_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public List<DietResponseForm> getMyDiets() {
        AppUser appUser = userSecurityService.getLoggedUser();
        List<Diet> diets = dietRepository.findAllByAppUser(appUser);

        List<DietResponseForm> dietResponseForms = new ArrayList<>();
        diets.forEach(diet -> {
            DietResponseForm dietResponseForm = new DietResponseForm();

            dietResponseForm.setIdDiet(diet.getIdDiet());
            dietResponseForm.setFinalDays(new ArrayList<>());

            diet.getFinalDays().forEach(finalDay -> {
                FinalDayResponseForm finalDayResponseForm = new FinalDayResponseForm();
                finalDayResponseForm.setFinalMeals(new ArrayList<>());

                finalDay.getFinalMeals().forEach(finalMeal -> {
                    FinalMealResponseForm finalMealResponseForm = new FinalMealResponseForm();
                    finalMealResponseForm.setName(finalMeal.getMeal().getName());
                    finalMealResponseForm.setImageUrl(ApplicationConstants.DEFAULT_MEAL_IMAGE_URL_WITH_PARAMETER + finalMeal.getMeal().getIdMeal());

                    finalDayResponseForm.getFinalMeals().add(finalMealResponseForm);
                });
                dietResponseForm.getFinalDays().add(finalDayResponseForm);
                dietResponseForm.setDietFileUrl(ApplicationConstants.DEFAULT_DIET_PDF_URL_WITH_PARAMETER + diet.getIdDiet());
                dietResponseForm.setShoppingListFileUrl(ApplicationConstants.DEFAULT_GROCERIES_PDF_URL_WITH_PARAMETER + diet.getIdDiet());
            });

            dietResponseForms.add(dietResponseForm);
        });

        return dietResponseForms;
    }

    public List<MealResponseBody> getUnreviewedMealsUsedByUser() {
        AppUser appUser = userSecurityService.getLoggedUser();
        List<Diet> diets = dietRepository.findAllByAppUser(appUser);

        Set<Meal> meals = new HashSet<>();
        diets.forEach(diet -> {
            diet.getFinalDays().forEach(finalDay -> {
                finalDay.getFinalMeals().forEach(finalMeal -> {
                    if(finalMeal.getMeal().getMealExtention().getReviews().stream().map(Review::getAuthor).noneMatch(appUser1 -> Objects.equals(appUser1.getIdUser(), userSecurityService.getLoggedUserId())))
                        meals.add(finalMeal.getMeal());
                });
            });
        });

        List<MealResponseBody> mealResponseBodies = new ArrayList<>();
        meals.forEach(meal -> {
            MealResponseBody mealResponseBody = new MealResponseBody(meal.getIdMeal(),
                    meal.getName(),
                    ApplicationConstants.DEFAULT_MEAL_IMAGE_URL_WITH_PARAMETER + meal.getIdMeal(),
                    meal.getIngredients().keySet().stream().map(Ingredient::getName).collect(Collectors.toList()),
                    meal.getCategories().stream().map(Category::getName).collect(Collectors.toList()),
                    meal.getAvgRating(),
                    meal.getMealExtention().getProteinRatio(),
                    meal.getTimesUsed()
            );

            mealResponseBodies.add(mealResponseBody);
        });

        return mealResponseBodies;
    }


    public List<MealResponseBody> getAlreadyReviewedMealsUsedByUser() {
        AppUser appUser = userSecurityService.getLoggedUser();
        List<Diet> diets = dietRepository.findAllByAppUser(appUser);

        Set<Meal> meals = new HashSet<>();
        diets.forEach(diet -> {
            diet.getFinalDays().forEach(finalDay -> {
                finalDay.getFinalMeals().forEach(finalMeal -> {
                    if(finalMeal.getMeal().getMealExtention().getReviews().stream().map(Review::getAuthor).noneMatch(appUser1 -> Objects.equals(appUser1.getIdUser(), userSecurityService.getLoggedUserId())))
                        meals.add(finalMeal.getMeal());
                });
            });
        });

        List<MealResponseBody> mealResponseBodies = new ArrayList<>();
        meals.forEach(meal -> {
            MealResponseBody mealResponseBody = new MealResponseBody(meal.getIdMeal(),
                    meal.getName(),
                    ApplicationConstants.DEFAULT_MEAL_IMAGE_URL_WITH_PARAMETER + meal.getIdMeal(),
                    meal.getIngredients().keySet().stream().map(Ingredient::getName).collect(Collectors.toList()),
                    meal.getCategories().stream().map(Category::getName).collect(Collectors.toList()),
                    meal.getAvgRating(),
                    meal.getMealExtention().getProteinRatio(),
                    meal.getTimesUsed());
            mealResponseBodies.add(mealResponseBody);
        });

        return mealResponseBodies;
    }

    public void deleteDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NO_CONTENT));

        if(!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        this.dietRepository.delete(diet);
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
