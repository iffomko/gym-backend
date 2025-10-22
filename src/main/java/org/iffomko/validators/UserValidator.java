package org.iffomko.validators;

import org.iffomko.domain.User;
import org.iffomko.exceptions.LocalizedException;

public class UserValidator {
    public static final int PASSWORD_MIN_SIZE = 6;

    private static final String INVALID_FORMAT_PHONE_NUMBER_MESSAGE = "validation.user.phone.invalid";
    private static final String INVALID_FORMAT_PASSWORD_MESSAGE = "validation.user.password.invalid";

    public void validate(User user) {
        if (user.getPhone() == null || user.getPhone().isEmpty() && phoneIsValid(user.getPhone())) {
            throw new LocalizedException(INVALID_FORMAT_PHONE_NUMBER_MESSAGE);
        }
        if (user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().isBlank()
                || user.getPassword().length() < PASSWORD_MIN_SIZE) {
            throw new LocalizedException(INVALID_FORMAT_PASSWORD_MESSAGE);
        }
    }

    private boolean phoneIsValid(String phone) {
        String formattedPhone = phone.startsWith("+") ? phone.substring(1) : phone;
        return (formattedPhone.startsWith("7") || formattedPhone.startsWith("8")) && formattedPhone.length() == 11;
    }
}
