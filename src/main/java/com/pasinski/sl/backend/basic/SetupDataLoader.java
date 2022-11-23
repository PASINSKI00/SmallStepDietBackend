package com.pasinski.sl.backend.basic;

import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.MealExtention;
import com.pasinski.sl.backend.meal.MealRepository;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.category.CategoryRepository;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.AppUserRepository;
import com.pasinski.sl.backend.user.accessManagment.Privilege;
import com.pasinski.sl.backend.user.accessManagment.PrivilegeRepository;
import com.pasinski.sl.backend.user.accessManagment.Role;
import com.pasinski.sl.backend.user.accessManagment.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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
        ingredient.setName("Chicken");
        ingredient.setCalories(100);
        ingredient.setProtein(10);
        ingredient.setCarbs(10);
        ingredient.setFats(2);
        ingredientRepository.save(ingredient);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Rice");
        ingredient2.setCalories(200);
        ingredient2.setProtein(10);
        ingredient2.setCarbs(10);
        ingredient2.setFats(13);
        ingredientRepository.save(ingredient2);
    }

    private void addCategories(){
        Category category = new Category();
        category.setName("Fast");
        categoryRepository.save(category);
    }

    private void addMeals(){
        Meal meal = new Meal();
        meal.setName("Meal1");
        meal.setImage("/assets/images/Hot_meal_header.png");
        meal.setMealExtention(new MealExtention());
        meal.getMealExtention().setRecipe("Cook the chicken. Boil the rice. Voila!");
        meal.getMealExtention().setTimeToPrepare(20);
        meal.getMealExtention().setCarbsRatio(40);
        meal.getMealExtention().setProteinRatio(40);
        meal.getMealExtention().setFatsRatio(20);
        List<Category> categories = new ArrayList<>();
        categories.add(categoryRepository.findByName("Fast"));
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientRepository.findByName("Chicken"));
        ingredients.add(ingredientRepository.findByName("Rice"));
        meal.setCategories(categories);
        meal.setIngredients(ingredients);
        mealRepository.save(meal);
    }

}
