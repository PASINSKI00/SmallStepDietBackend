package com.pasinski.sl.backend.meal.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewResponseBody {
    private String image;
    private String name;
    private int rating;
    private String content;
}
