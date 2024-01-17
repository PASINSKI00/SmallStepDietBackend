package com.pasinski.sl.backend.diet.forms.request;

import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;

import javax.validation.constraints.NotNull;

public record FinalDietModifyUnauthenticatedRequestForm(
        @NotNull
        FinalDietModifyRequestForm dietModifyForm,
        @NotNull
        BodyInfoForm bodyInfoForm
) { }