package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.meal.mealIngredientSpecifics.MealIngredientSpecifics;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.category.CategoryRepository;
import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.forms.MealResponseBody;
import com.pasinski.sl.backend.meal.forms.MealResponseBodyExtended;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.security.UserSecurityService;
import com.pasinski.sl.backend.user.AppUserRepository;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class MealService {
    private final MealRepository mealRepository;
    private final CategoryRepository categoryRepository;
    private final IngredientRepository ingredientRepository;
    private final AppUserRepository appUserRepository;
    private final UserSecurityService userSecurityService;

    public List<MealResponseBody> getMeals() {
        List<Meal> meals = mealRepository.findAll();
        List<MealResponseBody> mealResponseBodies = new ArrayList<>();

        meals.forEach(meal -> {
            mealResponseBodies.add(new MealResponseBody(
                    meal.getIdMeal(),
                    meal.getName(),
                    ApplicationConstants.DEFAULT_MEAL_IMAGE_URL_WITH_PARAMETER + meal.getIdMeal(),
                    meal.getIngredients().keySet().stream().map(Ingredient::getName).toList(),
                    meal.getCategories().stream().map(Category::getName).toList()
        ));
        });

        return mealResponseBodies;
    }

    public Long addMeal(MealForm mealForm) {
        Meal meal = new Meal();

        HashMap<Ingredient, MealIngredientSpecifics> ingredients = new HashMap<>();
        mealForm.getIngredients().forEach((id, amount) -> {
            MealIngredientSpecifics mealIngredientSpecifics = new MealIngredientSpecifics();
            Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
            mealIngredientSpecifics.setInitialWeight(amount);
            ingredients.put(ingredient, mealIngredientSpecifics);
        });

        meal.setName(mealForm.getName());
        meal.setIngredients(ingredients);

        meal.getMealExtention().setRecipe(mealForm.getRecipe());
        meal.getMealExtention().setTimeToPrepare(mealForm.getTimeToPrepare());

        if(mealForm.getCategoriesIds() != null)
            meal.setCategories(categoryRepository.findAllById(mealForm.getCategoriesIds()));

        if(mealForm.getImageName() != null)
            meal.setImageName(mealForm.getImageName());

        calculateProteinRatioOfAMeal(meal);
        assignCategoriesAutomatically(meal);

        meal.setAuthor(appUserRepository.findById(userSecurityService.getLoggedUserId()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND)));
        mealRepository.save(meal);

        return meal.getIdMeal();
    }

    public void updateMeal(MealForm mealForm) {
        Meal meal = mealRepository.findById(mealForm.getIdMeal()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        if (!Objects.equals(meal.getAuthor().getIdUser(), userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        if(mealForm.getName() != null)
            meal.setName(mealForm.getName());

        if(mealForm.getIngredients() != null) {
            HashMap<Ingredient, MealIngredientSpecifics> ingredients = new HashMap<>();
            mealForm.getIngredients().forEach((id, amount) -> {
                Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));
                MealIngredientSpecifics mealIngredientSpecifics = new MealIngredientSpecifics();
                mealIngredientSpecifics.setInitialWeight(amount);
                ingredients.put(ingredient, mealIngredientSpecifics);
            });
            meal.setIngredients(ingredients);
            calculateProteinRatioOfAMeal(meal);
        }

        if(mealForm.getRecipe() != null)
            meal.getMealExtention().setRecipe(mealForm.getRecipe());

        if(mealForm.getTimeToPrepare() != null)
            meal.getMealExtention().setTimeToPrepare(mealForm.getTimeToPrepare());

        if(mealForm.getCategoriesIds() != null) {
            meal.setCategories(categoryRepository.findAllById(mealForm.getCategoriesIds()));
            assignCategoriesAutomatically(meal);
        }

        if(mealForm.getImageName() != null)
            meal.setImageName(mealForm.getImageName());

        mealRepository.save(meal);
    }
    public void deleteMeal(Long idMeal) {
        Meal meal = mealRepository.findById(idMeal).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NO_CONTENT));

        if (!Objects.equals(meal.getAuthor().getIdUser(), userSecurityService.getLoggedUserId()))
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);

        mealRepository.delete(meal);
    }

    public MealResponseBodyExtended extendMeal(MealForm mealForm) {
        Meal meal = mealRepository.findById(mealForm.getIdMeal()).orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND));

        return new MealResponseBodyExtended(meal, meal.getMealExtention());
    }

    private void calculateProteinRatioOfAMeal(Meal meal) {
        //TODO
    }

    private void assignCategoriesAutomatically(Meal meal) {
        //TODO
    }
}
