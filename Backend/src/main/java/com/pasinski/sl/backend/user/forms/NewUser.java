package com.pasinski.sl.backend.user.forms;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
@Getter
public class NewUser {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    private String password;
}
