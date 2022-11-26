package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;

import javax.validation.constraints.NotNull;

@Getter
public class CategoryForm {
    @NotNull
    private String name;
}
