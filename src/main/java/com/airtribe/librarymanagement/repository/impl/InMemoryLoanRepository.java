package com.airtribe.librarymanagement.repository.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.airtribe.librarymanagement.entity.Loan;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class InMemoryLoanRepository implements LoanRepository {
    // Required by plan: fast O(1) availability checks
    private final Map<String, Loan> activeLoanByIsbn = new HashMap<>();

    private final Map<String, Loan> byLoanId = new HashMap<>();

    // Patron history (includes returned loans too) as stable ordering of loan ids
    private final Map<String, List<String>> loanIdsByPatronId = new HashMap<>();

    @Override
    public void save(Loan loan) {
        boolean isNew = !byLoanId.containsKey(loan.getLoanId());
        byLoanId.put(loan.getLoanId(), loan);

        // keep history (only once per loan)
        if (isNew) {
            loanIdsByPatronId
                    .computeIfAbsent(loan.getPatronId(), ignored -> new ArrayList<>())
                    .add(loan.getLoanId());
        }

        // update active index
        if (loan.isActive()) {
            activeLoanByIsbn.put(loan.getIsbn(), loan);
        } else {
            activeLoanByIsbn.remove(loan.getIsbn());
        }
    }

    @Override
    public Optional<Loan> findActiveLoanByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        return Optional.ofNullable(activeLoanByIsbn.get(key));
    }

    @Override
    public List<Loan> findLoansByPatronId(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        List<String> ids = loanIdsByPatronId.getOrDefault(key, List.of());
        List<Loan> loans = new ArrayList<>(ids.size());
        for (String id : ids) {
            Loan loan = byLoanId.get(id);
            if (loan != null) {
                loans.add(loan);
            }
        }
        return Collections.unmodifiableList(loans);
    }

    @Override
    public List<Loan> findActiveLoansByPatronId(String patronId) {
        List<Loan> all = findLoansByPatronId(patronId);
        List<Loan> active = new ArrayList<>();
        for (Loan loan : all) {
            if (loan.isActive()) {
                active.add(loan);
            }
        }
        return Collections.unmodifiableList(active);
    }

    @Override
    public List<Loan> findAllActiveLoans() {
        return List.copyOf(activeLoanByIsbn.values());
    }
}


