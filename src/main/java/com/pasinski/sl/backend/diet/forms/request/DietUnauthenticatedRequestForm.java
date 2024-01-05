package com.pasinski.sl.backend.diet.forms.request;

import com.pasinski.sl.backend.user.bodyinfo.forms.BodyInfoForm;

import java.util.List;

public record DietUnauthenticatedRequestForm(
        List<List<Long>> days,
        BodyInfoForm bodyInfo
) { }
