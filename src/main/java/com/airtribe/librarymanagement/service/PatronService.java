package com.airtribe.librarymanagement.service;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.airtribe.librarymanagement.entity.Loan;
import com.airtribe.librarymanagement.entity.Patron;
import com.airtribe.librarymanagement.exception.NotFoundException;
import com.airtribe.librarymanagement.exception.RuleViolationException;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.repository.PatronRepository;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class PatronService {
    private static final Logger logger = Logger.getLogger(PatronService.class.getName());

    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;

    public PatronService(PatronRepository patronRepository, LoanRepository loanRepository) {
        this.patronRepository = Objects.requireNonNull(patronRepository, "patronRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
    }

    public void addPatron(Patron patron) {
        Objects.requireNonNull(patron, "patron");
        if (patronRepository.existsById(patron.getPatronId())) {
            throw new RuleViolationException("Patron already exists: " + patron.getPatronId());
        }
        patronRepository.save(patron);
        logger.info("Added patron: " + patron.getPatronId());
    }

    public void updatePatron(String patronId, String name, String contact) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        Patron patron = patronRepository.findById(key).orElseThrow(() -> new NotFoundException("Patron not found: " + key));
        patron.updateInfo(name, contact);
        patronRepository.save(patron);
        logger.info("Updated patron: " + key);
    }

    public Patron getPatronById(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        return patronRepository.findById(key).orElseThrow(() -> new NotFoundException("Patron not found: " + key));
    }

    public List<Patron> listAllPatrons() {
        return List.copyOf(patronRepository.findAll());
    }

    public List<Loan> getBorrowHistory(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        if (!patronRepository.existsById(key)) {
            throw new NotFoundException("Patron not found: " + key);
        }
        return loanRepository.findLoansByPatronId(key);
    }
}


