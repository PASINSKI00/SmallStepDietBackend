package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
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
    @ColumnDefault("'/assets/images/Hot_meal_header.png'")
    private String image = "/assets/images/Hot_meal_header.png";

    @ManyToMany
    private List<Ingredient> ingredients = new ArrayList<>();

    @ManyToMany
    private List<Category> categories = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    MealExtention mealExtention = new MealExtention();

    @ManyToOne
    private AppUser author;
}
