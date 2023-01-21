package com.pasinski.sl.backend.basic;

import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealExtention;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.mealIngredientSpecifics.MealIngredientSpecificsRepository;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.category.CategoryRepository;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.meal.mealIngredientSpecifics.MealIngredientSpecifics;
import com.pasinski.sl.backend.meal.review.Review;
import com.pasinski.sl.backend.meal.review.ReviewRepository;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import com.pasinski.sl.backend.user.accessManagment.Privilege;
import com.pasinski.sl.backend.user.accessManagment.PrivilegeRepository;
import com.pasinski.sl.backend.user.accessManagment.Role;
import com.pasinski.sl.backend.user.accessManagment.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    boolean alreadySetup = false;

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MealRepository mealRepository;
    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final MealIngredientSpecificsRepository mealIngredientSpecificsRepository;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup)
            return;

        addUsers();
        addIngredients();
        addCategories();
        addMeals();

        alreadySetup = true;
    }

    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }

    private void addUsers(){
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        AppUser user = new AppUser();
        user.setName("Charlie");
        user.setPassword(passwordEncoder.encode("Password1!"));
        user.setEmail("email@email.com");
        user.setRoles(Arrays.asList(adminRole));
        appUserRepository.save(user);
    }

    private void addIngredients(){
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Chicken breast");
        ingredient.setCaloriesPer100g(164);
        ingredient.setProteinPer100g(31);
        ingredient.setCarbsPer100g(0);
        ingredient.setFatsPer100g(4);
        ingredientRepository.save(ingredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("White rice");
        ingredient2.setCaloriesPer100g(130);
        ingredient2.setProteinPer100g(3);
        ingredient2.setCarbsPer100g(28);
        ingredient2.setFatsPer100g(0);
        ingredientRepository.save(ingredient2);
    }

    private void addCategories(){
        addCategory("Breakfast");
        addCategory("Lunch");
        addCategory("Dinner");
        addCategory("Snack");
        addCategory("Dessert");
        addCategory("Shake");
        addCategory("Fast food");
        addCategory("Soup");
        addCategory("Salad");
        addCategory("Bread");
        addCategory("Pasta");
        addCategory("Pizza");
        addCategory("Sandwich");
        addCategory("Sauce");
        addCategory("Drink");
    }

    private void addCategory(String name){
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
    }

    private void addMeals(){
        addMeal("Meal1");
        addMeal("Meal2");
        addMeal("Meal3");
        addMeal("Meal4");
        addMeal("Meal5");
        addMeal("Meal6");
        addMeal("Meal7");
        addMeal("Meal8");
        addMeal("Meal9");
        addMeal("Meal10");
        addMeal("Meal11");
        addMeal("Meal12");
    }

    private void addMeal(String name){
        Meal meal = new Meal();
        meal.setName(name);
        meal.setMealExtention(new MealExtention());
        meal.getMealExtention().setRecipe("Cook the chicken. Boil the rice. Voila!");
        meal.getMealExtention().setTimeToPrepare(20);
        meal.getMealExtention().setCarbsRatio(40);
        meal.getMealExtention().setProteinRatio(40);
        meal.getMealExtention().setFatsRatio(20);
        List<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName("Fast"));
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientRepository.findByName("Chicken breast"));
        ingredients.add(ingredientRepository.findByName("White rice"));
        meal.setCategories(categories);
        HashMap<Ingredient, MealIngredientSpecifics> ingredientWeightHashMap = new HashMap<>();

        MealIngredientSpecifics mealIngredientSpecifics = new MealIngredientSpecifics();
        mealIngredientSpecifics.setInitialWeight(100);
        mealIngredientSpecifics.setInitialRatioInMeal(50);
        mealIngredientSpecificsRepository.save(mealIngredientSpecifics);
        ingredientWeightHashMap.put(ingredientRepository.findByName("Chicken breast"), mealIngredientSpecifics);

        MealIngredientSpecifics mealIngredientSpecifics2 = new MealIngredientSpecifics();
        mealIngredientSpecifics2.setInitialWeight(200);
        mealIngredientSpecifics2.setInitialRatioInMeal(50);
        mealIngredientSpecificsRepository.save(mealIngredientSpecifics2);
        ingredientWeightHashMap.put(ingredientRepository.findByName("White rice"), mealIngredientSpecifics2);

        meal.setIngredients(ingredientWeightHashMap);
        meal.setAuthor(appUserRepository.findById(1L).get());
        meal.setInitialCalories();
        mealRepository.save(meal);
    }

}
