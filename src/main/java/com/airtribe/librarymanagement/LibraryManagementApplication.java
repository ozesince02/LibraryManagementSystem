package com.airtribe.librarymanagement;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.airtribe.librarymanagement.cli.LibraryCli;
import com.airtribe.librarymanagement.factory.DefaultLoanFactory;
import com.airtribe.librarymanagement.factory.LoanFactory;
import com.airtribe.librarymanagement.policy.LendingPolicy;
import com.airtribe.librarymanagement.repository.BookRepository;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.repository.NotificationRepository;
import com.airtribe.librarymanagement.repository.PatronRepository;
import com.airtribe.librarymanagement.repository.ReservationRepository;
import com.airtribe.librarymanagement.repository.impl.InMemoryBookRepository;
import com.airtribe.librarymanagement.repository.impl.InMemoryLoanRepository;
import com.airtribe.librarymanagement.repository.impl.InMemoryNotificationRepository;
import com.airtribe.librarymanagement.repository.impl.InMemoryPatronRepository;
import com.airtribe.librarymanagement.repository.impl.InMemoryReservationRepository;
import com.airtribe.librarymanagement.service.BookService;
import com.airtribe.librarymanagement.service.LendingService;
import com.airtribe.librarymanagement.service.NotificationService;
import com.airtribe.librarymanagement.service.PatronService;
import com.airtribe.librarymanagement.service.RecommendationService;
import com.airtribe.librarymanagement.service.ReservationService;

public final class LibraryManagementApplication {
    private static final Logger logger = Logger.getLogger(LibraryManagementApplication.class.getName());

    private LibraryManagementApplication() {
    }

    public static void main(String[] args) {
        logger.setLevel(Level.INFO);
        logger.info("Starting Library Management System (CLI)...");

        // Repositories
        BookRepository bookRepository = new InMemoryBookRepository();
        PatronRepository patronRepository = new InMemoryPatronRepository();
        LoanRepository loanRepository = new InMemoryLoanRepository();
        ReservationRepository reservationRepository = new InMemoryReservationRepository();
        NotificationRepository notificationRepository = new InMemoryNotificationRepository();

        // Policy 
        LendingPolicy lendingPolicy = new LendingPolicy(3, 14); // max 3 books, 14-day loan

        // Factory pattern: encapsulate how loans are created
        LoanFactory loanFactory = new DefaultLoanFactory();

        // Services
        BookService bookService = new BookService(bookRepository, loanRepository);
        PatronService patronService = new PatronService(patronRepository, loanRepository);
        RecommendationService recommendationService = new RecommendationService(bookRepository, patronRepository, loanRepository);
        NotificationService notificationService = new NotificationService(notificationRepository);
        ReservationService reservationService = new ReservationService(
                reservationRepository,
                bookRepository,
                patronRepository,
                loanRepository,
                notificationService
        );
        LendingService lendingService = new LendingService(
                bookRepository,
                patronRepository,
                loanRepository,
                lendingPolicy,
                loanFactory,
                reservationService
        );

        LibraryCli cli = new LibraryCli(bookService, patronService, lendingService, recommendationService, reservationService, notificationService);
        cli.run();

        logger.info("Exiting Library Management System.");
    }
}


