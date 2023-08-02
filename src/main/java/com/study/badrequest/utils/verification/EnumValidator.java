package com.study.badrequest.utils.verification;

import com.study.badrequest.common.annotation.EnumValid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


public class EnumValidator implements ConstraintValidator<EnumValid, Enum> {

    private EnumValid annotation;

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(Enum value, ConstraintValidatorContext context) {
        boolean result = false;
        Enum<?>[] enums = this.annotation.enumClass().getEnumConstants();
        if (enums != null) {
            for (Object e : enums) {
                if (value == e) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }
}
