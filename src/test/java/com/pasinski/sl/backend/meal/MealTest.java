package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.mealIngredient.MealIngredient;
import com.pasinski.sl.backend.user.AppUser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MealTest {

    @Test
    void createMeal_setsValuesProperly_True() {
        // Given
        // Filled mealForm
        MealForm mealForm = new MealForm();
        mealForm.setName("Meal");
        mealForm.setTimeToPrepare(30);
        mealForm.setRecipe("Recipe");

        // Set Ingredients
        List<MealIngredient> ingredients = new ArrayList<>();

        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setInitialWeight(111);
        Ingredient chickenBreast = new Ingredient();
        chickenBreast.setCaloriesPer100g(164F);
        chickenBreast.setProteinPer100g(31F);
        chickenBreast.setFatsPer100g(3.6F);
        chickenBreast.setCarbsPer100g(0F);
        mealIngredient.setIngredient(chickenBreast);
        ingredients.add(mealIngredient);

        MealIngredient mealIngredient2 = new MealIngredient();
        mealIngredient2.setInitialWeight(158);
        Ingredient whiteRice = new Ingredient();
        whiteRice.setCaloriesPer100g(130F);
        whiteRice.setProteinPer100g(2.7F);
        whiteRice.setFatsPer100g(0.3F);
        whiteRice.setCarbsPer100g(28F);
        mealIngredient2.setIngredient(whiteRice);
        ingredients.add(mealIngredient2);

        // When
        Meal meal = new Meal(mealForm, ingredients, List.of(new Category()), new AppUser());

        // Then
        assertEquals(387, meal.getInitialCalories());
        assertEquals(39, meal.getMealExtention().getProteinRatio()); //154.704 kcal
        assertEquals(10, meal.getMealExtention().getFatsRatio()); //40.23 kcal
        assertEquals(45, meal.getMealExtention().getCarbsRatio());// 176.96 kcal
    }

    @Test
    void modifyMeal_modifiesValuesProperly_True() {
        // Given
        // Filled mealForm
        MealForm mealForm = new MealForm();
        mealForm.setName("Meal");
        mealForm.setTimeToPrepare(30);
        mealForm.setRecipe("Recipe");

        // Set Ingredients
        List<MealIngredient> ingredients = new ArrayList<>();

        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setInitialWeight(111);
        Ingredient chickenBreast = new Ingredient();
        chickenBreast.setCaloriesPer100g(164F);
        chickenBreast.setProteinPer100g(31F);
        chickenBreast.setFatsPer100g(3.6F);
        chickenBreast.setCarbsPer100g(0F);
        mealIngredient.setIngredient(chickenBreast);
        ingredients.add(mealIngredient);

        MealIngredient mealIngredient2 = new MealIngredient();
        mealIngredient2.setInitialWeight(158);
        Ingredient whiteRice = new Ingredient();
        whiteRice.setCaloriesPer100g(130F);
        whiteRice.setProteinPer100g(2.7F);
        whiteRice.setFatsPer100g(0.3F);
        whiteRice.setCarbsPer100g(28F);
        mealIngredient2.setIngredient(whiteRice);
        ingredients.add(mealIngredient2);

        Meal meal = new Meal(mealForm, ingredients, List.of(new Category()), new AppUser());

        // When
        mealForm.setName("Modified meal");
        mealForm.setTimeToPrepare(30);
        mealForm.setRecipe("Modified recipe");

        mealIngredient.setInitialWeight(222);
        mealIngredient2.setInitialWeight(316);

        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        category.setName("Category");
        categories.add(category);

        List<MealIngredient> ingredients2 = new ArrayList<>();
        mealIngredient2.setInitialWeight(200);
        ingredients2.add(mealIngredient2);

        meal.modify(mealForm, ingredients2, categories);

        // Then
        assertEquals("Modified meal", meal.getName());
        assertEquals(30, meal.getMealExtention().getTimeToPrepare());
        assertEquals("Modified recipe", meal.getMealExtention().getRecipe());
        assertEquals(1, meal.getIngredients().size());
        assertEquals(mealIngredient2, meal.getIngredients().get(0));
        assertEquals(1, meal.getCategories().size());
        assertEquals(category, meal.getCategories().get(0));
        assertEquals(260, meal.getInitialCalories());
    }
}