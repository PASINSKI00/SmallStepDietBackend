package com.pasinski.sl.backend.user.bodyinfo;

import com.pasinski.sl.backend.user.AppUser;
import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;
import com.pasinski.sl.backend.user.bodyinfo.forms.Goals;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class BodyInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_body_info", nullable = false)
    private Long idBodyInfo;

    @NotNull
    @Column(nullable = false)
    private Goals goal;

    @NotNull
    @Column(nullable = false)
    private Integer height;

    @NotNull
    @Column(nullable = false)
    private Integer weight;

    @NotNull
    @Column(nullable = false)
    private Integer age;

    @NotNull
    @Column(nullable = false)
    private Gender gender;

    @NotNull
    @Column(nullable = false)
    private Float pal;

    @NotNull
    @Column(nullable = false)
    private Integer additionalCalories;

    private Double TDEE;
    private Double BEE;
    private Integer CaloriesGoal;

    @NotNull
    @OneToOne(mappedBy = "bodyInfo", optional = false)
    private AppUser appUser;

    public BodyInfo(BodyInfoForm bodyInfoForm, AppUser appUser) {
        this.goal = bodyInfoForm.goal();
        this.height = bodyInfoForm.height();
        this.weight = bodyInfoForm.weight();
        this.age = bodyInfoForm.age();
        this.gender = bodyInfoForm.gender();
        this.pal = bodyInfoForm.pal();
        this.additionalCalories = bodyInfoForm.additionalCalories();
        this.appUser = appUser;
        performCalculations();
    }

    private void performCalculations() {
        this.BEE = calculateBEE();
        this.TDEE = calculateTDEE();
        this.CaloriesGoal = calculateCaloriesGoal();
    }

    private Double calculateBEE() {
        int constant = -161;

        if (gender.equals(Gender.MALE))
            constant = 5;

        return 10 * getWeight() + 6.25 * getHeight() - 5 * getAge() + constant;
    }

    private Double calculateTDEE() {
        return Math.round(getBEE() * getPal() * 1000) / 1000.0;
    }


    private Integer calculateCaloriesGoal() {
        Double multiplierBasedOnGoal = switch (getGoal()) {
            case LOSE_WEIGHT -> 0.9;
            case MAINTAIN_WEIGHT -> 1.0;
            case GAIN_WEIGHT -> 1.1;
        };

        return (int) (getTDEE() * multiplierBasedOnGoal + getAdditionalCalories());
    }
}
