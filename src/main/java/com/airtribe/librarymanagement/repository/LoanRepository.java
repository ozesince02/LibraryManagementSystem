package com.airtribe.librarymanagement.repository;

import java.util.List;
import java.util.Optional;

import com.airtribe.librarymanagement.entity.Loan;

public interface LoanRepository {
    void save(Loan loan);

    Optional<Loan> findActiveLoanByIsbn(String isbn);

    List<Loan> findLoansByPatronId(String patronId);

    List<Loan> findActiveLoansByPatronId(String patronId);

    List<Loan> findAllActiveLoans();
}


