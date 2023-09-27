package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.config.security.UserSecurityService;
import com.pasinski.sl.backend.file.FileType;
import com.pasinski.sl.backend.file.S3Service;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.category.CategoryRepository;
import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.forms.MealResponseBodyExtended;
import com.pasinski.sl.backend.meal.forms.ReviewForm;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.meal.mealIngredient.MealIngredient;
import com.pasinski.sl.backend.meal.review.Review;
import com.pasinski.sl.backend.meal.review.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final ReviewRepository reviewRepository;
    private final UserSecurityService userSecurityService;
    private final S3Service s3Service;

    public List<MealResponseBody> getMeals(String nameContains, String sortBy, List<String> categories, int pageNumber,
                                           int pageSize) {
        Pageable pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        List<Category> categoriesList = categoryRepository.findAllByNameIn(categories);
        categoriesList = categoriesList.isEmpty() ? null : categoriesList;
        Long categoriesSize = categoriesList == null ? 0 : (long) categoriesList.size();

        return mealRepository
                .findMealsByNameAndCategories(nameContains, categoriesList, categoriesSize, pageable).stream()
                .map(meal -> new MealResponseBody(meal, s3Service))
                .sorted(sortMap(sortBy)).collect(Collectors.toList());
    }

    public Long addMeal(MealForm mealForm) {
        if (mealForm.getCategoriesIds() == null || mealForm.getCategoriesIds().isEmpty())
            mealForm.setCategoriesIds(List.of(0L));

        return mealRepository.save(new Meal(mealForm, getMealIngredientsFromMealForm(mealForm), categoryRepository.findAllById(mealForm.getCategoriesIds()), userSecurityService.getLoggedUser())).getIdMeal();
    }

    public void addReview(ReviewForm reviewForm) {
        Meal meal = mealRepository.findById(reviewForm.getIdMeal()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
        Review review = new Review();
        review.setRating(reviewForm.getRating());
        review.setComment(reviewForm.getComment());
        review.setAuthor(this.userSecurityService.getLoggedUser());
        this.reviewRepository.save(review);

        meal.getMealExtention().getReviews().add(review);
        meal.setAvgRating((float) (meal.getMealExtention().getReviews().stream().map(Review::getRating).reduce(0, Integer::sum) / meal.getMealExtention().getReviews().size()));

        mealRepository.save(meal);
    }

    public void updateMeal(MealForm mealForm) {
        Meal meal = mealRepository.findById(mealForm.getIdMeal()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(meal.getAuthor().getIdUser(), userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        if (mealForm.getCategoriesIds() == null || mealForm.getCategoriesIds().isEmpty())
            mealForm.setCategoriesIds(List.of(0L));

        meal.modify(mealForm, getMealIngredientsFromMealForm(mealForm), categoryRepository.findAllById(mealForm.getCategoriesIds()));

        mealRepository.save(meal);
    }

    public void deleteMeal(Long idMeal) {
        Meal meal = mealRepository.findById(idMeal).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NO_CONTENT));

        if (!Objects.equals(meal.getAuthor().getIdUser(), userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        mealRepository.delete(meal);
    }

    public MealResponseBodyExtended extendMeal(Long idMeal) {
        Meal meal = mealRepository.findById(idMeal).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        return new MealResponseBodyExtended(meal, meal.getMealExtention());
    }

    public void setImageBooleanValue(Long idMeal, Boolean value) {
        Meal meal = mealRepository.findById(idMeal).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(meal.getAuthor().getIdUser(), userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        meal.setImageSet(value);
        mealRepository.save(meal);
    }

    private List<MealIngredient> getMealIngredientsFromMealForm(MealForm mealForm) {
        List<MealIngredient> ingredients = new ArrayList<>();

        mealForm.getIngredients().forEach((id, amount) -> {
            Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
            MealIngredient mealIngredient = new MealIngredient(ingredient, amount);
            ingredients.add(mealIngredient);
        });

        return ingredients;
    }

    private Comparator<MealResponseBody> sortMap(String sortBy){
        return switch (sortBy) {
            case "Protein percent" -> Comparator.comparing(MealResponseBody::getProteinRatio).reversed();
            case "Ranking" -> Comparator.comparing(MealResponseBody::getAvgRating).reversed();
            case "Popularity" -> Comparator.comparing(MealResponseBody::getTimesUsed).reversed();
            default -> Comparator.comparing(MealResponseBody::getIdMeal);
        };
    }
}
