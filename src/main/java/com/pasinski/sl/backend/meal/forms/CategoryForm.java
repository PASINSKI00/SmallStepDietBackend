package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;

import javax.validation.constraints.NotNull;

public record CategoryForm (
    @NotNull
    String name
) {}
