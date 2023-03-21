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

    private Integer TDEE;
    private Integer BEE;
    private Integer CaloriesGoal;

    @NotNull
    @OneToOne(mappedBy = "bodyInfo", optional = false)
    private AppUser appUser;

    public BodyInfo(BodyInfoForm bodyInfoForm, AppUser appUser) {
        this.goal = bodyInfoForm.getGoal();
        this.height = bodyInfoForm.getHeight();
        this.weight = bodyInfoForm.getWeight();
        this.age = bodyInfoForm.getAge();
        this.gender = bodyInfoForm.getGender();
        this.pal = bodyInfoForm.getPal();
        this.additionalCalories = bodyInfoForm.getAdditionalCalories();
        this.appUser = appUser;
        performCalculations();
    }

    private void performCalculations() {
        this.BEE = calculateBEE();
        this.TDEE = calculateTDEE();
        this.CaloriesGoal = calculateCaloriesGoal();
    }

    private Integer calculateBEE() {
        int constant = -161;

        if (gender.equals(Gender.MALE))
            constant = 5;

        return (int) (10 * getWeight() + 6.25 * getHeight() - 5 * getAge() + constant);
    }

    private Integer calculateTDEE() {
        return (int) (getBEE() * getPal());
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
