package com.pasinski.sl.backend.user.bodyinfo.forms;

import lombok.Getter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Getter
public class BodyInfoForm {
    @NotNull
    private Goals goal;

    @NotNull
    private Integer height;

    @NotNull
    private Integer weight;

    @NotNull
    private Integer age;

    @NotNull
    @DecimalMin(value = "1.39", message = "PAL must be between 1.4 and 2.4")
    @DecimalMax(value = "2.41", message = "PAL must be between 1.4 and 2.4")
    private Float pal;

    @NotNull
    private Integer additionalCalories;
}
