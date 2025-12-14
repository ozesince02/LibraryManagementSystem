package com.airtribe.librarymanagement.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.airtribe.librarymanagement.entity.Loan;
import com.airtribe.librarymanagement.exception.NotFoundException;
import com.airtribe.librarymanagement.exception.RuleViolationException;
import com.airtribe.librarymanagement.factory.LoanFactory;
import com.airtribe.librarymanagement.policy.LendingPolicy;
import com.airtribe.librarymanagement.repository.BookRepository;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.repository.PatronRepository;
import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class LendingService {
    private static final Logger logger = Logger.getLogger(LendingService.class.getName());

    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;
    private final LendingPolicy lendingPolicy;
    private final LoanFactory loanFactory;
    private final ReservationService reservationService;

    public LendingService(
            BookRepository bookRepository,
            PatronRepository patronRepository,
            LoanRepository loanRepository,
            LendingPolicy lendingPolicy,
            LoanFactory loanFactory,
            ReservationService reservationService
    ) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.patronRepository = Objects.requireNonNull(patronRepository, "patronRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
        this.lendingPolicy = Objects.requireNonNull(lendingPolicy, "lendingPolicy");
        this.loanFactory = Objects.requireNonNull(loanFactory, "loanFactory");
        this.reservationService = Objects.requireNonNull(reservationService, "reservationService");
    }

    public Loan checkout(String patronId, String isbn) {
        String patronKey = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        String isbnKey = IsbnValidator.validateAndNormalize(isbn);

        if (!patronRepository.existsById(patronKey)) {
            throw new NotFoundException("Patron not found: " + patronKey);
        }
        if (!bookRepository.existsByIsbn(isbnKey)) {
            throw new NotFoundException("Book not found: " + isbnKey);
        }
        if (loanRepository.findActiveLoanByIsbn(isbnKey).isPresent()) {
            throw new RuleViolationException("Book is already borrowed: " + isbnKey);
        }

        // Reservation rule: if there is a queue, only the next patron can checkout.
        reservationService.validateAndConsumeIfEligible(patronKey, isbnKey);

        List<Loan> activeLoans = loanRepository.findActiveLoansByPatronId(patronKey);
        if (activeLoans.size() >= lendingPolicy.getMaxLoansPerPatron()) {
            throw new RuleViolationException("Borrow limit reached for patron: " + patronKey);
        }

        LocalDate checkoutDate = LocalDate.now();
        Loan loan = loanFactory.createLoan(patronKey, isbnKey, checkoutDate, lendingPolicy);
        loanRepository.save(loan);

        logger.info("Checked out book " + isbnKey + " to patron " + patronKey + " due " + loan.getDueDate());
        return loan;
    }

    public Loan returnBook(String patronId, String isbn) {
        String patronKey = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        String isbnKey = IsbnValidator.validateAndNormalize(isbn);

        Loan loan = loanRepository.findActiveLoanByIsbn(isbnKey)
                .orElseThrow(() -> new RuleViolationException("Book is not currently borrowed: " + isbnKey));

        if (!loan.getPatronId().equals(patronKey)) {
            throw new RuleViolationException("This book was borrowed by a different patron. Expected " +
                    loan.getPatronId() + " but got " + patronKey);
        }

        loan.markReturned(LocalDate.now());
        loanRepository.save(loan);

        // Notify next reservation (if any) now that book is available
        reservationService.onBookReturned(isbnKey);

        logger.info("Returned book " + isbnKey + " from patron " + patronKey);
        return loan;
    }

    public boolean isAvailable(String isbn) {
        String isbnKey = IsbnValidator.validateAndNormalize(isbn);
        return bookRepository.existsByIsbn(isbnKey) && loanRepository.findActiveLoanByIsbn(isbnKey).isEmpty();
    }

    public List<Loan> listAllActiveLoans() {
        return loanRepository.findAllActiveLoans();
    }
}


