package com.pasinski.sl.backend.user.bodyinfo.forms;

import com.pasinski.sl.backend.user.bodyinfo.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
    private Gender gender;

    @NotNull
    @DecimalMin(value = "1.39", message = "PAL must be between 1.4 and 2.5")
    @DecimalMax(value = "2.51", message = "PAL must be between 1.4 and 2.5")
    private Float pal;

    @NotNull
    private Integer additionalCalories;
}
