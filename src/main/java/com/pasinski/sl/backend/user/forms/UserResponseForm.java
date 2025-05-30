package com.pasinski.sl.backend.user.forms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseForm {
    String name;
    String imageUrl;

    public UserResponseForm(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }
}
