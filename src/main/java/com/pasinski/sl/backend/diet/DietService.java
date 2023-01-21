package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.diet.PDFGenerator.PDFGeneratorService;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.forms.*;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.review.Review;
import com.pasinski.sl.backend.security.UserSecurityService;
import com.pasinski.sl.backend.user.AppUser;
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
    private final UserSecurityService userSecurityService;
    private final PDFGeneratorService pdfGeneratorService;

    public DietResponseForm getDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(!Objects.equals(diet.getAppUser().getIdUser(), this.userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

       return new DietResponseForm(diet);
    }

    public Long addDiet(List<List<Long>> daysForm) {
        if(userSecurityService.getLoggedUser().getBodyInfo().getCaloriesGoal() == null)
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "You have to set your body info first");

        return this.dietRepository.save(new Diet(getListOfListsOfMeals(daysForm), userSecurityService.getLoggedUser())).getIdDiet();
    }

    public void updateDiet(Long idDiet, List<List<Long>> daysForm) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(!Objects.equals(diet.getAppUser().getIdUser(), this.userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        diet.updateDiet(getListOfListsOfMeals(daysForm));

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

        return diet.getGroceries().stream().toList();
    }

    public String generateGroceriesPDF(Long idDiet) throws FileNotFoundException {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if(!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return this.pdfGeneratorService.generateGroceriesPDF(diet.getGroceries().stream().toList());
    }

    public InputStreamResource getGroceriesPdf(String fileName) throws FileNotFoundException {
        File file = new File(ApplicationConstants.PATH_TO_PDF_DIRECTORY + FileSystems.getDefault().getSeparator() + fileName);

        if (!file.exists())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);

        return new InputStreamResource(new FileInputStream(file));
    }

    public List<DietResponseForm> getMyDiets() {
        AppUser appUser = userSecurityService.getLoggedUser();

        return dietRepository.findAllByAppUser(appUser).stream().map(DietResponseForm::new).toList();
    }

    public List<MealResponseBody> getUnreviewedMealsUsedByUser() {
        AppUser appUser = userSecurityService.getLoggedUser();

        return dietRepository.findAllByAppUser(appUser).stream()
                .flatMap(diet -> diet.getFinalDays().stream())
                .flatMap(finalDay -> finalDay.getFinalMeals().stream())
                .map(FinalMeal::getMeal)
                .filter(meal -> meal.getMealExtention().getReviews().stream().map(Review::getAuthor).noneMatch(appUser1 -> Objects.equals(appUser1.getIdUser(), userSecurityService.getLoggedUserId()))).collect(Collectors.toSet())
                .stream()
                .map(MealResponseBody::new)
                .collect(Collectors.toList());
    }

    public List<MealResponseBody> getAlreadyReviewedMealsUsedByUser() {
        AppUser appUser = userSecurityService.getLoggedUser();

        return dietRepository.findAllByAppUser(appUser).stream()
                .flatMap(diet -> diet.getFinalDays().stream())
                .flatMap(finalDay -> finalDay.getFinalMeals().stream())
                .map(FinalMeal::getMeal)
                .filter(meal -> meal.getMealExtention().getReviews().stream().map(Review::getAuthor).anyMatch(appUser1 -> Objects.equals(appUser1.getIdUser(), userSecurityService.getLoggedUserId()))).collect(Collectors.toSet())
                .stream()
                .map(MealResponseBody::new)
                .collect(Collectors.toList());
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

    private List<List<Meal>> getListOfListsOfMeals(List<List<Long>> daysForm){
        List<List<Meal>> days = new ArrayList<>();
        daysForm.forEach(day -> {
            days.add(new ArrayList<>());
            day.forEach(idMeal -> {
                Meal meal = this.mealRepository.findById(idMeal).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
                days.get(days.size() - 1).add(meal);
            });
        });

        return days;
    }

    private void modifyPercentagesOfMealsForDay(DietResponseForm dietResponseForm, Diet diet) {
//        Verify that the sum of percentages is 100
        dietResponseForm.getFinalDays().forEach(finalDay -> {
            if(finalDay.getFinalMeals().stream().map(FinalMealResponseForm::getPercentOfDay).reduce(0, Integer::sum) != 100)
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
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
