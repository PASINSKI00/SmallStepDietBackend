package com.pasinski.sl.backend.user.bodyinfo.forms;

import com.pasinski.sl.backend.user.bodyinfo.BodyInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BodyInfoResponseForm {
    private Goals goal;
    private Integer height;
    private Integer weight;
    private Integer age;
    private Float pal;
    private Integer additionalCalories;
    private Integer TDEE;
    private Integer BEE;
    private Integer caloriesGoal;

    public BodyInfoResponseForm(BodyInfo bodyInfo) {
        this.goal = bodyInfo.getGoal();
        this.height = bodyInfo.getHeight();
        this.weight = bodyInfo.getWeight();
        this.age = bodyInfo.getAge();
        this.pal = bodyInfo.getPal();
        this.additionalCalories = bodyInfo.getAdditionalCalories();
        this.TDEE = bodyInfo.getTDEE();
        this.BEE = bodyInfo.getBEE();
        this.caloriesGoal = bodyInfo.getCaloriesGoal();
    }
}
