package com.pasinski.sl.backend.basic;

import com.pasinski.sl.backend.meal.MealService;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.category.CategoryRepository;
import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import com.pasinski.sl.backend.user.accessManagment.Privilege;
import com.pasinski.sl.backend.user.accessManagment.PrivilegeRepository;
import com.pasinski.sl.backend.user.accessManagment.Role;
import com.pasinski.sl.backend.user.accessManagment.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
@RequiredArgsConstructor
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final PasswordEncoder passwordEncoder;
    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;
    private final MealService mealService;
    private final AuthenticationManager authenticationManager;
    boolean alreadySetup = false;

    @Autowired
    private Environment environment;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (alreadySetup || !environment.matchesProfiles("local"))
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

    private void addUsers() {
        Privilege readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Collections.singletonList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        AppUser user = new AppUser();
        user.setName("Admin");
        user.setPassword(passwordEncoder.encode("Password1"));
        user.setEmail("admin@email.com");
        user.setRoles(Collections.singletonList(adminRole));
        appUserRepository.save(user);
    }

    private void addIngredients() {
        Ingredient ingredient = new Ingredient();
        ingredient.setName("Chicken breast");
        ingredient.setCaloriesPer100g(164F);
        ingredient.setProteinPer100g(31F);
        ingredient.setCarbsPer100g(0F);
        ingredient.setFatsPer100g(4F);
        ingredientRepository.save(ingredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("White rice");
        ingredient2.setCaloriesPer100g(130F);
        ingredient2.setProteinPer100g(3F);
        ingredient2.setCarbsPer100g(28F);
        ingredient2.setFatsPer100g(0F);
        ingredientRepository.save(ingredient2);
    }

    private void addCategories() {
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

    private void addCategory(String name) {
        Category category = new Category();
        category.setName(name);
        categoryRepository.save(category);
    }

    private void addMeals() {
        for(int i = 0; i < 100; i++) {
            addMeal("Meal " + i);
        }
    }

    private void addMeal(String name) {
        MealForm mealForm = new MealForm();
        mealForm.setName(name);
        mealForm.setRecipe("Cook the chicken. Boil the rice. Voila!");
        mealForm.setTimeToPrepare(20);
        mealForm.setCategoriesIds(Arrays.asList(1L, 2L));
        HashMap<Long, Integer> ingredientsIds = new HashMap<>();
        ingredientsIds.put(1L, 100);
        ingredientsIds.put(2L, 150);
        mealForm.setIngredients(ingredientsIds);

        UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken("admin@email.com", "Password1");
        Authentication authentication = authenticationManager.authenticate(authReq);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        this.mealService.addMeal(mealForm);

//        Meal meal = new Meal(mealForm, );
//        meal.setName(name);
//        meal.setMealExtention(new MealExtention());
//        meal.getMealExtention().setRecipe("Cook the chicken. Boil the rice. Voila!");
//        meal.getMealExtention().setTimeToPrepare(20);
//        meal.getMealExtention().setCarbsRatio(40);
//        meal.getMealExtention().setProteinRatio(40);
//        meal.getMealExtention().setFatsRatio(20);
//        List<Category> categories = new ArrayList<>();
//        categories.add(categoryRepository.findByName("Fast"));
//        List<Ingredient> ingredients = new ArrayList<>();
//        ingredients.add(ingredientRepository.findByName("Chicken breast"));
//        ingredients.add(ingredientRepository.findByName("White rice"));
//        meal.setCategories(categories);
//        HashMap<Ingredient, MealIngredientSpecifics> ingredientWeightHashMap = new HashMap<>();
//
//        MealIngredientSpecifics mealIngredientSpecifics = new MealIngredientSpecifics();
//        mealIngredientSpecifics.setInitialWeight(100);
//        mealIngredientSpecifics.setInitialRatioInMeal(50);
//        mealIngredientSpecificsRepository.save(mealIngredientSpecifics);
//        ingredientWeightHashMap.put(ingredientRepository.findByName("Chicken breast"), mealIngredientSpecifics);
//
//        MealIngredientSpecifics mealIngredientSpecifics2 = new MealIngredientSpecifics();
//        mealIngredientSpecifics2.setInitialWeight(200);
//        mealIngredientSpecifics2.setInitialRatioInMeal(50);
//        mealIngredientSpecificsRepository.save(mealIngredientSpecifics2);
//        ingredientWeightHashMap.put(ingredientRepository.findByName("White rice"), mealIngredientSpecifics2);
//
//        meal.setIngredients(ingredientWeightHashMap);
//        meal.setAuthor(appUserRepository.findById(1L).get());
//        meal.setInitialCalories();
//        mealRepository.save(meal);
    }

}
