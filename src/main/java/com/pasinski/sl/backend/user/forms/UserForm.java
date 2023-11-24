package com.pasinski.sl.backend.user.forms;

import com.pasinski.sl.backend.util.annotations.Password;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record UserForm (
    @NotBlank(message = "Name is mandatory")
    String name,

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email,

    @NotBlank(message = "Password is mandatory")
    @Password
    String password
) {}
