package com.pasinski.sl.backend.diet.forms.request;

import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;

import javax.validation.constraints.NotNull;
import java.util.List;

public record DietUnauthenticatedRequestForm(
        @NotNull
        List<List<Long>> days,
        @NotNull
        BodyInfoForm bodyInfoForm
) { }
