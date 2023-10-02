package com.pasinski.sl.backend.file;

import lombok.Getter;

@Getter
public enum FileType {
    DIET_PDF("pdfs/diets/"),
    GROCERIES_PDF("pdfs/groceries/"),
    MEAL_IMAGE("images/meals/"),
    USER_IMAGE("images/users/");

    private final String path;

    FileType(String path) {
        this.path = path;
    }
}
