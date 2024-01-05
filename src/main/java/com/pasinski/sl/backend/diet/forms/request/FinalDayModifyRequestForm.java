package com.pasinski.sl.backend.diet.forms.request;

import java.util.List;

public record FinalDayModifyRequestForm(
        Long idFinalDay,
        List<FinalMealModifyRequestForm> finalMeals
) { }
