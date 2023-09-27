package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.PDFGenerator.PDFService;
import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import com.pasinski.sl.backend.diet.forms.DietResponseForm;
import com.pasinski.sl.backend.diet.forms.Grocery;
import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.meal.review.Review;
import com.pasinski.sl.backend.user.AppUser;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.io.FileNotFoundException;
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
    private final PDFService pdfService;
    private final IngredientRepository ingredientRepository;
    private final S3Service s3Service;

    public DietResponseForm getDiet(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(diet.getAppUser().getIdUser(), this.userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return new DietResponseForm(diet, s3Service);
    }

    public Long addDiet(List<List<Long>> daysForm) {
        if (userSecurityService.getLoggedUser().getBodyInfo() == null)
            throw new HttpClientErrorException(HttpStatus.UNPROCESSABLE_ENTITY, "You have to set your body info first");

        return this.dietRepository.save(new Diet(getListOfListsOfMeals(daysForm), userSecurityService.getLoggedUser())).getIdDiet();
    }

    public void updateDiet(Long idDiet, List<List<Long>> daysForm) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(diet.getAppUser().getIdUser(), this.userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        diet.updateDiet(getListOfListsOfMeals(daysForm));

        this.dietRepository.save(diet);
    }

    public String generateDietPDF(Long idDiet) throws FileNotFoundException {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if(diet.getPdfName() != null)
            return this.s3Service.getFileUrl(diet.getPdfName(), FileType.DIET_PDF);

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        String pdfName = this.pdfService.generateDietPDF(diet);
        diet.setPdfName(pdfName);
        this.dietRepository.save(diet);

        return this.s3Service.getFileUrl(pdfName, FileType.DIET_PDF);
    }

    public List<Grocery> getGroceries(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return diet.getGroceries().stream().toList();
    }

    public String generateGroceriesPDF(Long idDiet) throws FileNotFoundException {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if(diet.getGroceriesPdfName() != null)
            return this.s3Service.getFileUrl(diet.getGroceriesPdfName(), FileType.GROCERIES_PDF);

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        String pdfName = this.pdfService.generateGroceriesPDF(diet.getGroceries().stream().toList(), diet.getIdDiet());
        diet.setGroceriesPdfName(pdfName);
        this.dietRepository.save(diet);

        return this.s3Service.getFileUrl(pdfName, FileType.GROCERIES_PDF);
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

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        this.pdfService.deleteDietPDFs(diet.getPdfName(), diet.getGroceriesPdfName());
        this.dietRepository.delete(diet);
    }

    public void modifyFinalDiet(DietResponseForm modifiedDiet) {
        Diet diet = this.dietRepository.findById(modifiedDiet.getIdDiet()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        diet.modifyDiet(modifiedDiet, ingredientRepository);
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

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        diet.resetDay(idDay);
        this.dietRepository.save(diet);
    }

    public String getGroceriesPDFUrl(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if(diet.getGroceriesPdfName() == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "PDF not found");

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return this.s3Service.getFileUrl(diet.getGroceriesPdfName(), FileType.GROCERIES_PDF);
    }

    public String getDietPDFUrl(Long idDiet) {
        Diet diet = this.dietRepository.findById(idDiet).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        if(diet.getPdfName() == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "PDF not found");

        if (!Objects.equals(this.userSecurityService.getLoggedUserId(), diet.getAppUser().getIdUser()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        return this.s3Service.getFileUrl(diet.getPdfName(), FileType.DIET_PDF);
    }
}
