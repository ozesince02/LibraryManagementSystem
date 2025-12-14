package com.airtribe.librarymanagement.factory;

import java.time.LocalDate;

import com.airtribe.librarymanagement.entity.Loan;
import com.airtribe.librarymanagement.policy.LendingPolicy;

/**
 * Factory pattern: encapsulates how a Loan is created.
 */
public interface LoanFactory {
    Loan createLoan(String patronId, String isbn, LocalDate checkoutDate, LendingPolicy policy);
}


