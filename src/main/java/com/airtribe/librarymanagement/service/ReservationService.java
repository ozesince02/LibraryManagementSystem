package com.airtribe.librarymanagement.service;

import com.airtribe.librarymanagement.entity.Reservation;
import com.airtribe.librarymanagement.exception.NotFoundException;
import com.airtribe.librarymanagement.exception.RuleViolationException;
import com.airtribe.librarymanagement.repository.BookRepository;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.repository.PatronRepository;
import com.airtribe.librarymanagement.repository.ReservationRepository;
import com.airtribe.librarymanagement.util.IdGenerator;
import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public final class ReservationService {
    private static final Logger logger = Logger.getLogger(ReservationService.class.getName());

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;
    private final NotificationService notificationService;

    public ReservationService(
            ReservationRepository reservationRepository,
            BookRepository bookRepository,
            PatronRepository patronRepository,
            LoanRepository loanRepository,
            NotificationService notificationService
    ) {
        this.reservationRepository = Objects.requireNonNull(reservationRepository, "reservationRepository");
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.patronRepository = Objects.requireNonNull(patronRepository, "patronRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
        this.notificationService = Objects.requireNonNull(notificationService, "notificationService");
    }

    /**
     * Reserve a book that is currently checked out.
     */
    public Reservation reserveBook(String patronId, String isbn) {
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        String i = IsbnValidator.validateAndNormalize(isbn);

        if (!patronRepository.existsById(p)) {
            throw new NotFoundException("Patron not found: " + p);
        }
        if (!bookRepository.existsByIsbn(i)) {
            throw new NotFoundException("Book not found: " + i);
        }
        // Core requirement: reserve only if currently checked out
        if (loanRepository.findActiveLoanByIsbn(i).isEmpty()) {
            throw new RuleViolationException("Book is available; reservation is only allowed when the book is checked out: " + i);
        }
        if (reservationRepository.hasReservation(i, p)) {
            throw new RuleViolationException("You already have a reservation for this book: " + i);
        }

        Reservation reservation = new Reservation(IdGenerator.newId(), i, p, LocalDateTime.now());
        reservationRepository.enqueue(reservation);
        logger.info("Reserved book " + i + " for patron " + p);
        return reservation;
    }

    public List<Reservation> listReservationsForPatron(String patronId) {
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        if (!patronRepository.existsById(p)) {
            throw new NotFoundException("Patron not found: " + p);
        }
        return reservationRepository.findReservationsByPatronId(p);
    }

    public Optional<Reservation> peekNextReservation(String isbn) {
        String i = IsbnValidator.validateAndNormalize(isbn);
        return reservationRepository.peekNextByIsbn(i);
    }

    /**
     * Called when a book is returned (becomes available).
     * Sends a notification to the next patron in queue (if any).
     */
    public void onBookReturned(String isbn) {
        String i = IsbnValidator.validateAndNormalize(isbn);
        Reservation next = reservationRepository.peekNextByIsbn(i).orElse(null);
        if (next == null) {
            return;
        }
        notificationService.notifyPatron(
                next.getPatronId(),
                "Reserved book is now available. ISBN=" + i + ". Please checkout."
        );
        logger.info("Notified next reservation patron " + next.getPatronId() + " for isbn " + i);
    }

    /**
     * Enforced during checkout: if there is a reservation queue, only the first patron can checkout.
     * If that patron checks out, we consume the reservation.
     */
    public void validateAndConsumeIfEligible(String patronId, String isbn) {
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        String i = IsbnValidator.validateAndNormalize(isbn);

        Optional<Reservation> next = reservationRepository.peekNextByIsbn(i);
        if (next.isEmpty()) {
            return;
        }

        Reservation r = next.get();
        if (!r.getPatronId().equals(p)) {
            throw new RuleViolationException("Book is reserved by another patron. Next patron in queue: " + r.getPatronId());
        }

        // Consume (pop) the reservation because this patron is now checking out
        reservationRepository.dequeueNextByIsbn(i);
        logger.info("Consumed reservation for isbn " + i + " by patron " + p);
    }
}


