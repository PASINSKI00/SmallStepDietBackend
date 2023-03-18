package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.category.Category;
import com.pasinski.sl.backend.meal.forms.MealForm;
import com.pasinski.sl.backend.meal.mealIngredient.MealIngredient;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class Meal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_meal", nullable = false)
    private Long idMeal;

    @NotNull
    private String name;
    private boolean imageSet = false;

    @NotNull
    @ColumnDefault("0")
    private Float avgRating = 0F;

    @NotNull
    @ColumnDefault("0")
    private Integer timesUsed = 0;

    private Integer initialCalories;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealIngredient> ingredients;

    @ManyToMany
    private List<Category> categories;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    MealExtention mealExtention;

    @ManyToOne()
    private AppUser author;

    public Meal(MealForm mealForm, List<MealIngredient> ingredients, List<Category> categories, AppUser author) {
        this.name = mealForm.getName();
        this.ingredients = ingredients;
        this.categories = categories;
        this.author = author;
        this.initialCalories = this.ingredients.stream().reduce(0F, (sum, mealIngredient) -> sum + (mealIngredient.getIngredient().getCaloriesPer100g() * mealIngredient.getInitialWeight() / 100), Float::sum).intValue();
        int proteinRatio = (int) (ingredients.stream().reduce(0F, (sum, mealIngredient) -> sum + (mealIngredient.getIngredient().getProteinPer100g() * mealIngredient.getInitialWeight() / 100), Float::sum) * 4 / this.initialCalories * 100);
        int fatsRatio = (int) (ingredients.stream().reduce(0F, (sum, mealIngredient) -> sum + (mealIngredient.getIngredient().getFatsPer100g() * mealIngredient.getInitialWeight() / 100), Float::sum) * 9 / this.initialCalories * 100);
        int carbsRatio = (int) (ingredients.stream().reduce(0F, (sum, mealIngredient) -> sum + (mealIngredient.getIngredient().getCarbsPer100g() * mealIngredient.getInitialWeight() / 100), Float::sum) * 4 / this.initialCalories * 100);
        this.mealExtention = new MealExtention(mealForm.getRecipe(), mealForm.getTimeToPrepare(), proteinRatio, fatsRatio, carbsRatio);
    }

    public void modify(MealForm mealForm, List<MealIngredient> ingredients, List<Category> categories){
        if(mealForm.getName() != null)
            this.name = mealForm.getName();

        if(ingredients != null) {
            this.ingredients.removeAll(this.ingredients);
            this.ingredients.addAll(ingredients);
        }

        if(categories != null)
            this.categories = categories;

        if(mealForm.getRecipe() != null)
            this.mealExtention.setRecipe(mealForm.getRecipe());

        if(mealForm.getTimeToPrepare() != null)
            this.mealExtention.setTimeToPrepare(mealForm.getTimeToPrepare());
    }
}
