package org.iffomko.exceptions;

import java.util.ResourceBundle;

public class LocalizedException extends RuntimeException {
    private static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    public LocalizedException(String key, Object... args) {
        super(String.format(messages.getString(key), args));
    }

    public LocalizedException(String key, Throwable cause, Object... args) {
        super(String.format(messages.getString(key), args), cause);
    }
}
