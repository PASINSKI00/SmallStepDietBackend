package com.pasinski.sl.backend.post.forms;

import lombok.Getter;

import javax.validation.constraints.NotEmpty;

@Getter
public class PostForm {
    @NotEmpty
    private String content;
}
