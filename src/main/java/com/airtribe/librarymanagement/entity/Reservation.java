package com.airtribe.librarymanagement.entity;

import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Reservation {
    private final String reservationId; // identity
    private final String isbn;
    private final String patronId;
    private final LocalDateTime createdAt;

    public Reservation(String reservationId, String isbn, String patronId, LocalDateTime createdAt) {
        this.reservationId = ValidationUtils.requireNonBlank(reservationId, "Reservation ID");
        this.isbn = IsbnValidator.validateAndNormalize(isbn);
        this.patronId = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPatronId() {
        return patronId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", patronId='" + patronId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}


