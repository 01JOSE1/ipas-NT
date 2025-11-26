package com.ipas.ipas.validation;

import com.ipas.ipas.view.dto.PolicyRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

public class PolicyAmountsValidator implements ConstraintValidator<ValidPolicyAmounts, PolicyRequest> {

    @Override
    public void initialize(ValidPolicyAmounts constraintAnnotation) {
        // no initialization needed
    }

    @Override
    public boolean isValid(PolicyRequest value, ConstraintValidatorContext context) {
        if (value == null) return true;

        BigDecimal coverage = value.getCoverageAmount();
        BigDecimal siniestro = value.getValorSiniestro();

        // If no siniestro provided, it's valid
        if (siniestro == null) return true;

        // If coverage is null, let other validations handle it
        if (coverage == null) return true;

        if (siniestro.compareTo(coverage) > 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode("valorSiniestro")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
