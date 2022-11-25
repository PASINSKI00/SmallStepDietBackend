package com.pasinski.sl.backend.meal.forms;

import com.pasinski.sl.backend.meal.category.Category;
import lombok.Getter;

import java.util.Set;

@Getter
public class SearchForm {
    private Set<Category> categories;
    private String searchPhraze;
}
