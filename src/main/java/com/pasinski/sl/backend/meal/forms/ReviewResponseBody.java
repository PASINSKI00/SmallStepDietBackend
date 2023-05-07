package com.pasinski.sl.backend.meal.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReviewResponseBody {
    private String imageUrl;
    private String name;
    private int rating;
    private String content;
}
