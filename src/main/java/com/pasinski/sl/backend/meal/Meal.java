package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.ingredient.Ingredient;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    private String image;

    @OneToOne(cascade = CascadeType.ALL)
    MealExtention mealExtention;

    @ManyToMany
    private List<Ingredient> ingredients;

    @ManyToMany
    private List<Category> categories;
}
