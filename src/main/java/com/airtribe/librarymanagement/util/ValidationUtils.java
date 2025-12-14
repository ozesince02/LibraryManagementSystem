package com.airtribe.librarymanagement.util;

import com.airtribe.librarymanagement.exception.ValidationException;

public final class ValidationUtils {
    private ValidationUtils() {
    }

    public static String requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
        return value.trim();
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
        return value;
    }
}


