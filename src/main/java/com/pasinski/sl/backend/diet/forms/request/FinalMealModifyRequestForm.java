package com.pasinski.sl.backend.diet.forms.request;

import java.util.List;

public record FinalMealModifyRequestForm(
       Long idFinalMeal,
       List<FinalIngredientModifyRequestForm>finalIngredients,
       Integer percentOfDay
) { }
