package com.pasinski.sl.backend.diet.forms.request;

public record FinalIngredientModifyRequestForm(
        Long idFinalIngredient,
        String name,
        Integer weight,
        Long idNewIngredient,
        Boolean remove
) { }
