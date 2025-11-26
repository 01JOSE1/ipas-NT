package com.ipas.ipas.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = { PolicyAmountsValidator.class })
@Documented
public @interface ValidPolicyAmounts {
    String message() default "El valor del siniestro no puede ser superior al valor asegurado";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
