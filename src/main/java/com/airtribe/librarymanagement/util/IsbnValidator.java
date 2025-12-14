package com.airtribe.librarymanagement.util;

import com.airtribe.librarymanagement.exception.ValidationException;

public final class IsbnValidator {
    private IsbnValidator() {
    }

    /**
     * Accepts ISBN-10 or ISBN-13 digits, optionally with hyphens/spaces.
     * Note: checksum validation is intentionally skipped for assignment simplicity.
     */
    public static String validateAndNormalize(String rawIsbn) {
        String isbn = ValidationUtils.requireNonBlank(rawIsbn, "ISBN");
        String normalized = isbn.replace("-", "").replace(" ", "");

        if (!(normalized.length() == 10 || normalized.length() == 13)) {
            throw new ValidationException("ISBN must be 10 or 13 digits (hyphens/spaces allowed)");
        }
        for (int i = 0; i < normalized.length(); i++) {
            char c = normalized.charAt(i);
            if (c < '0' || c > '9') {
                throw new ValidationException("ISBN must contain digits only (after removing hyphens/spaces)");
            }
        }
        return normalized;
    }
}


