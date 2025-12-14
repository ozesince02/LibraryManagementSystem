package com.airtribe.librarymanagement.entity;

import java.time.LocalDate;
import java.util.Objects;

import com.airtribe.librarymanagement.exception.RuleViolationException;
import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class Loan {
    private final String loanId;
    private final String isbn;
    private final String patronId;
    private final LocalDate checkoutDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Loan(String loanId, String isbn, String patronId, LocalDate checkoutDate, LocalDate dueDate) {
        this.loanId = ValidationUtils.requireNonBlank(loanId, "Loan ID");
        this.isbn = IsbnValidator.validateAndNormalize(isbn);
        this.patronId = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        this.checkoutDate = Objects.requireNonNull(checkoutDate, "checkoutDate");
        this.dueDate = Objects.requireNonNull(dueDate, "dueDate");
        if (dueDate.isBefore(checkoutDate)) {
            throw new RuleViolationException("Due date cannot be before checkout date");
        }
    }

    public String getLoanId() {
        return loanId;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getPatronId() {
        return patronId;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public boolean isActive() {
        return returnDate == null;
    }

    public void markReturned(LocalDate returnDate) {
        if (!isActive()) {
            throw new RuleViolationException("Loan is already returned");
        }
        this.returnDate = Objects.requireNonNull(returnDate, "returnDate");
        if (this.returnDate.isBefore(checkoutDate)) {
            throw new RuleViolationException("Return date cannot be before checkout date");
        }
    }

    @Override
    public String toString() {
        return "Loan{" +
                "loanId='" + loanId + '\'' +
                ", isbn='" + isbn + '\'' +
                ", patronId='" + patronId + '\'' +
                ", checkoutDate=" + checkoutDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}


