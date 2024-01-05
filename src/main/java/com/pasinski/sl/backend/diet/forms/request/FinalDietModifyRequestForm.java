package com.pasinski.sl.backend.diet.forms.request;

import java.util.List;

public record FinalDietModifyRequestForm(
        Long idDiet,
        List<FinalDayModifyRequestForm>finalDays
) {
}
