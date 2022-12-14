package com.pasinski.sl.backend.diet.finalDay;

import com.pasinski.sl.backend.diet.finalMeal.FinalMeal;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
public class FinalDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_final_day", nullable = false)
    private Long idFinalDay;

    @OneToMany
    @Cascade(CascadeType.ALL)
    private List<FinalMeal> finalMeals;

    private Integer calories;
    private Integer protein;
    private Integer fats;
    private Integer carbs;
}
