package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.forms.Grocery;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.user.AppUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.annotations.CascadeType;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diet", nullable = false)
    private Long idDiet;

    @OneToMany(orphanRemoval = true)
    @Cascade(CascadeType.ALL)
    private List<FinalDay> finalDays;

    @ManyToOne
    private AppUser appUser;

    public Diet(List<List<Meal>> days, AppUser appUser) {
        this.appUser = appUser;
        this.finalDays = new ArrayList<>();
        days.forEach(meals -> this.finalDays.add(new FinalDay(meals, appUser.getBodyInfo().getCaloriesGoal())));
    }

    public void updateDiet(List<List<Meal>> days) {
        this.finalDays = new ArrayList<>();
        days.forEach(meals -> this.finalDays.add(new FinalDay(meals, appUser.getBodyInfo().getCaloriesGoal())));
    }

    public Set<Grocery> getGroceries(){
        return finalDays.stream()
                .flatMap(finalDay -> finalDay.getFinalMeals().stream())
                .flatMap(finalMeal -> finalMeal.getFinalIngredients().stream())
                .collect(Collectors.groupingBy(finalIngredient -> finalIngredient.getIngredient().getName()))
                .entrySet()
                .stream()
                .map(entry -> new Grocery(entry.getKey(), entry.getValue().stream().mapToInt(FinalIngredient::getWeight).sum()))
                .collect(Collectors.toSet());
    }
}
