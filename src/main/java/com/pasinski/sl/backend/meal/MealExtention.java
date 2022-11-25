package com.pasinski.sl.backend.meal;

import com.pasinski.sl.backend.meal.review.Review;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class MealExtention {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_meal_extention", nullable = false)
    private Long idMealExtention;
    private String recipe;
    private int timeToPrepare;
    private int proteinRatio;
    private int fatsRatio;
    private int carbsRatio;

    @OneToMany
    private List<Review> reviews = new ArrayList<>();

}
