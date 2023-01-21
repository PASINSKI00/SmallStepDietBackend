package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.basic.ApplicationConstants;
import com.pasinski.sl.backend.meal.mealIngredientSpecifics.MealIngredientSpecifics;
import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meal", nullable = false)
    private Long idMeal;

    @NotNull
    private String name;

    @NotNull
    private String imageName = ApplicationConstants.DEFAULT_MEAL_IMAGE_NAME;

    @NotNull
    @ColumnDefault("0")
    private Float avgRating = 0F;

    @NotNull
    @ColumnDefault("0")
    private Integer timesUsed = 0;

    private Integer initialCalories;

    @OneToMany
    @MapKeyJoinColumn(name = "id_ingredient")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private Map<Ingredient, MealIngredientSpecifics> ingredients;

    @ManyToMany
    private List<Category> categories = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    MealExtention mealExtention = new MealExtention();

    @ManyToOne(optional = true)
    private AppUser author;

    public void setInitialCalories() {
        final Integer[] calories = {0};

        this.ingredients.forEach((key, value) -> calories[0] += key.getCaloriesPer100g() * value.getInitialWeight() / 100);

        this.initialCalories = calories[0];
    }
}
