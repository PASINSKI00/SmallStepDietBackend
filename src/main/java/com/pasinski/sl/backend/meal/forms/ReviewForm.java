package com.pasinski.sl.backend.meal.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewForm {
    private Long idMeal;
    private Integer rating;
    private String comment;
}
