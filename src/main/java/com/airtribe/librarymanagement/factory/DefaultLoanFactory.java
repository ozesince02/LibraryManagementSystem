package com.airtribe.librarymanagement.factory;

import java.time.LocalDate;
import java.util.Objects;

import com.airtribe.librarymanagement.entity.Loan;
import com.airtribe.librarymanagement.policy.LendingPolicy;
import com.airtribe.librarymanagement.util.IdGenerator;
import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class DefaultLoanFactory implements LoanFactory {
    @Override
    public Loan createLoan(String patronId, String isbn, LocalDate checkoutDate, LendingPolicy policy) {
        String patronKey = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        String isbnKey = IsbnValidator.validateAndNormalize(isbn);
        LocalDate checkout = Objects.requireNonNull(checkoutDate, "checkoutDate");
        LendingPolicy p = Objects.requireNonNull(policy, "policy");

        LocalDate dueDate = checkout.plusDays(p.getLoanPeriodDays());
        return new Loan(IdGenerator.newId(), isbnKey, patronKey, checkout, dueDate);
    }
}


