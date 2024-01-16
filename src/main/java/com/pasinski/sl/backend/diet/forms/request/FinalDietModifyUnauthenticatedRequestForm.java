package com.pasinski.sl.backend.diet.forms.request;

import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;

public record FinalDietModifyUnauthenticatedRequestForm(
        FinalDietModifyRequestForm dietModifyForm,
        BodyInfoForm bodyInfoForm
) { }