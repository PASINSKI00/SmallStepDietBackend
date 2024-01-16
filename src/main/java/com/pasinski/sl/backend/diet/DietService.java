package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.forms.DietResponseForm;
import com.pasinski.sl.backend.diet.forms.request.DietUnauthenticatedRequestForm;
import com.pasinski.sl.backend.diet.forms.Grocery;
import com.pasinski.sl.backend.diet.forms.request.FinalDietModifyRequestForm;
import com.pasinski.sl.backend.diet.forms.request.FinalDietModifyUnauthenticatedRequestForm;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.meal.review.Review;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.bodyinfo.BodyInfo;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DietService {
    private final DietRepository dietRepository;
    private final MealRepository mealRepository;
    private final UserSecurityService userSecurityService;
    private final IngredientRepository ingredientRepository;
    private final S3Service s3Service;

    public DietResponseForm getDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        return new DietResponseForm(diet, s3Service);
    }

    public Long addDiet(List<List<Long>> daysForm) {
        if (userSecurityService.getLoggedUser().getBodyInfo() == null)
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "You have to set your body info first");

        Diet diet = new Diet(getListOfListsOfMeals(daysForm), userSecurityService.getLoggedUser());

        return this.dietRepository.save(diet).getIdDiet();
    }
    public Long addDietForUnauthenticated(DietUnauthenticatedRequestForm requestForm) {
        BodyInfo bodyInfo = new BodyInfo(requestForm.bodyInfoForm(), null);

        return this.dietRepository.save(new Diet(getListOfListsOfMeals(requestForm.days()), bodyInfo))
                .getIdDiet();
    }

    public void updateDiet(Long idDiet, List<List<Long>> daysForm) {
        Diet diet = this.dietRepository.findById(idDiet)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.updateDiet(getListOfListsOfMeals(daysForm));
        this.dietRepository.save(diet);
    }

    public void updateDietForUnauthenticated(Long idDiet, DietUnauthenticatedRequestForm requestForm) {
        BodyInfo bodyInfo = new BodyInfo(requestForm.bodyInfoForm(), null);
        Diet diet = this.dietRepository.findById(idDiet)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.updateDiet(getListOfListsOfMeals(requestForm.days()), bodyInfo);
        this.dietRepository.save(diet);
    }

    public List<Grocery> getGroceries(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        return diet.getGroceries().stream().toList();
    }

    public List<DietResponseForm> getMyDiets() {
        AppUser appUser = userSecurityService.getLoggedUser();

        return dietRepository.findAllByAppUser(appUser).stream().map(diet -> new DietResponseForm(diet, s3Service)).toList();
    }

    public List<MealResponseBody> getUnreviewedMealsUsedByUser() {
        AppUser appUser = userSecurityService.getLoggedUser();

        return dietRepository.findAllByAppUser(appUser).stream()
                .flatMap(diet -> diet.getFinalDays().stream())
                .flatMap(finalDay -> finalDay.getFinalMeals().stream())
                .map(FinalMeal::getMeal)
                .filter(meal -> meal.getMealExtention().getReviews().stream().map(Review::getAuthor).noneMatch(appUser1 -> Objects.equals(appUser1.getIdUser(), userSecurityService.getLoggedUserId()))).collect(Collectors.toSet())
                .stream()
                .map(meal -> new MealResponseBody(meal, s3Service))
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
                .map(meal -> new MealResponseBody(meal, s3Service))
                .collect(Collectors.toList());
    }

    public void deleteDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NO_CONTENT));

        validateOwner(diet);
        this.dietRepository.delete(diet);
    }

    public void modifyFinalDiet(FinalDietModifyRequestForm modifiedDiet) {
        Diet diet = this.dietRepository.findById(modifiedDiet.idDiet())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.modifyDiet(modifiedDiet, ingredientRepository);
        this.dietRepository.save(diet);
    }

    public void modifyFinalDietForUnauthenticated(FinalDietModifyUnauthenticatedRequestForm modifiedDiet) {
        BodyInfo bodyInfo = new BodyInfo(modifiedDiet.bodyInfoForm(), null);
        Diet diet = this.dietRepository.findById(modifiedDiet.dietModifyForm().idDiet())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.modifyDiet(modifiedDiet.dietModifyForm(), ingredientRepository, bodyInfo);
        this.dietRepository.save(diet);
    }

    private List<List<Meal>> getListOfListsOfMeals(List<List<Long>> daysForm) {
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

    public void resetDay(Long idDiet, Long idDay) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.resetDay(idDay);
        this.dietRepository.save(diet);
    }

    public void resetDay(Long idDiet, Long idDay, BodyInfoForm bodyInfoForm) {
        BodyInfo bodyInfo = new BodyInfo(bodyInfoForm, null);
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.resetDay(idDay, bodyInfo);
        this.dietRepository.save(diet);
    }

    public void reCalculate(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        validateOwner(diet);
        diet.reCalculate();
        this.dietRepository.save(diet);
    }

    private void validateOwner(Diet diet) {
        if (diet.getAppUser() != null && !Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
    }
}
