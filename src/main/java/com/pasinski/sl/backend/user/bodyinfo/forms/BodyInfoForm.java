package com.pasinski.sl.backend.user.bodyinfo.forms;

import com.pasinski.sl.backend.user.bodyinfo.Gender;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

public record BodyInfoForm (
    @NotNull
    Goals goal,

    @NotNull
    Integer height,

    @NotNull
    Integer weight,

    @NotNull
    @DecimalMin(value = "16", message = "You must be at least 16 years old to use our service")
    Integer age,

    @NotNull
    Gender gender,

    @NotNull
    @DecimalMin(value = "1.39", message = "PAL must be between 1.4 and 2.5")
    @DecimalMax(value = "2.51", message = "PAL must be between 1.4 and 2.5")
    Float pal,

    @NotNull
    Integer additionalCalories
) {}
