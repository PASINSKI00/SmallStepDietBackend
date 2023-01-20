package com.pasinski.sl.backend.user.bodyinfo.forms;

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
}
