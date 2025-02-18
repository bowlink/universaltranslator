package com.hel.ut.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneConstraintValidator implements ConstraintValidator<Phone, String> {

    @Override
    public void initialize(Phone String) {
    }

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext cxt) {
        if (phoneField == null) {
            return false;
        }
        return phoneField.matches("[0-9()-]*");
    }
}