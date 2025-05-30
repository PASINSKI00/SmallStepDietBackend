package com.pasinski.sl.backend.diet;

import com.pasinski.sl.backend.diet.finalDay.FinalDay;
import com.pasinski.sl.backend.diet.finalIngredient.FinalIngredient;
import com.pasinski.sl.backend.diet.forms.Grocery;
import com.pasinski.sl.backend.diet.forms.request.FinalDietModifyRequestForm;
import com.pasinski.sl.backend.meal.Meal;
import com.pasinski.sl.backend.meal.ingredient.IngredientRepository;
import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.bodyinfo.BodyInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Diet(List<List<Meal>> days, BodyInfo bodyInfo) {
        this.appUser = null;
        this.finalDays = new ArrayList<>();
        days.forEach(meals -> this.finalDays.add(new FinalDay(meals, bodyInfo.getCaloriesGoal())));
    }

    public void reCalculate() {
        this.finalDays.forEach(finalDay -> this.resetDay(finalDay.getIdFinalDay()));
    }

    public void resetDay(Long idDay) {
        this.finalDays.stream().filter(finalDay -> Objects.equals(finalDay.getIdFinalDay(), idDay))
                .findFirst().ifPresentOrElse(finalDay -> finalDay.resetDay(appUser.getBodyInfo().getCaloriesGoal()),
                        () -> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND); });
    }
    public void resetDay(Long idDay, BodyInfo bodyInfo) {
        this.finalDays.stream().filter(finalDay -> Objects.equals(finalDay.getIdFinalDay(), idDay))
                .findFirst().ifPresentOrElse(finalDay -> finalDay.resetDay(bodyInfo.getCaloriesGoal()),
                        () -> { throw new HttpClientErrorException(HttpStatus.NOT_FOUND); });
    }

    public void updateDiet(List<List<Meal>> days) {
        this.finalDays.clear();
        days.forEach(meals -> this.finalDays.add(new FinalDay(meals, appUser.getBodyInfo().getCaloriesGoal())));
    }

    public void updateDiet(List<List<Meal>> days, BodyInfo bodyInfo) {
        this.finalDays.clear();
        days.forEach(meals -> this.finalDays.add(new FinalDay(meals, bodyInfo.getCaloriesGoal())));
    }

    public void modifyDiet(FinalDietModifyRequestForm modifiedDiet, IngredientRepository ingredientRepository) {
        modifiedDiet.finalDays().forEach(modifiedDay -> {
            finalDays.forEach(finalDay -> {
                if (Objects.equals(modifiedDay.idFinalDay(), finalDay.getIdFinalDay()))
                    finalDay.modifyFinalDay(modifiedDay, ingredientRepository, appUser.getBodyInfo().getCaloriesGoal());
            });
        });
    }

    public void modifyDiet(FinalDietModifyRequestForm modifiedDiet, IngredientRepository ingredientRepository, BodyInfo bodyInfo) {
        modifiedDiet.finalDays().forEach(modifiedDay -> {
            finalDays.forEach(finalDay -> {
                if (Objects.equals(modifiedDay.idFinalDay(), finalDay.getIdFinalDay()))
                    finalDay.modifyFinalDay(modifiedDay, ingredientRepository, bodyInfo.getCaloriesGoal());
            });
        });
    }

    public Set<Grocery> getGroceries() {
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
