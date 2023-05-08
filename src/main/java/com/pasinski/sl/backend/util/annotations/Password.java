package com.pasinski.sl.backend.util.annotations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password {

    String message() default "Password should be at least 8 characters long, contain at least one uppercase letter, one lowercase letter, one digit";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}