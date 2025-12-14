package com.airtribe.librarymanagement.cli;

import java.util.Locale;
import java.util.Scanner;
import java.util.logging.Logger;

import com.airtribe.librarymanagement.service.BookService;
import com.airtribe.librarymanagement.service.LendingService;
import com.airtribe.librarymanagement.service.NotificationService;
import com.airtribe.librarymanagement.service.PatronService;
import com.airtribe.librarymanagement.service.RecommendationService;
import com.airtribe.librarymanagement.service.ReservationService;

/**
 * Thin CLI adapter. Business logic will be wired in later via services.
 */
public final class LibraryCli {
    private static final Logger logger = Logger.getLogger(LibraryCli.class.getName());

    private final BookService bookService;
    private final PatronService patronService;
    private final LendingService lendingService;
    private final RecommendationService recommendationService;
    private final ReservationService reservationService;
    private final NotificationService notificationService;
    private final Scanner scanner;

    public LibraryCli(
            BookService bookService,
            PatronService patronService,
            LendingService lendingService,
            RecommendationService recommendationService,
            ReservationService reservationService,
            NotificationService notificationService
    ) {
        this.bookService = bookService;
        this.patronService = patronService;
        this.lendingService = lendingService;
        this.recommendationService = recommendationService;
        this.reservationService = reservationService;
        this.notificationService = notificationService;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            printMenu();
            String input = readLine("Select option: ").trim().toLowerCase(Locale.ROOT);

            switch (input) {
                case "1" -> booksMenu();
                case "2" -> patronsMenu();
                case "3" -> lendingMenu();
                case "q", "quit", "exit" -> {
                    return;
                }
                default -> logger.warning("Unknown option: " + input);
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Library Management System ===");
        System.out.println("1) Book management");
        System.out.println("2) Patron management");
        System.out.println("3) Lending");
        System.out.println("q) Quit");
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private void booksMenu() {
        while (true) {
            System.out.println();
            System.out.println("--- Book Management ---");
            System.out.println("1) Add book");
            System.out.println("2) Update book");
            System.out.println("3) Remove book");
            System.out.println("4) Search books");
            System.out.println("5) List all books");
            System.out.println("b) Back");

            String input = readLine("Select option: ").trim().toLowerCase(Locale.ROOT);
            try {
                switch (input) {
                    case "1" -> addBookFlow();
                    case "2" -> updateBookFlow();
                    case "3" -> removeBookFlow();
                    case "4" -> searchBooksFlow();
                    case "5" -> listAllBooksFlow();
                    case "b", "back" -> {
                        return;
                    }
                    default -> System.out.println("Unknown option: " + input);
                }
            } catch (RuntimeException ex) {
                logger.warning(ex.getMessage());
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void patronsMenu() {
        while (true) {
            System.out.println();
            System.out.println("--- Patron Management ---");
            System.out.println("1) Add patron");
            System.out.println("2) Update patron");
            System.out.println("3) List all patrons");
            System.out.println("4) View patron borrow history");
            System.out.println("5) Get recommendations");
            System.out.println("6) View my reservations");
            System.out.println("7) View my notifications");
            System.out.println("b) Back");

            String input = readLine("Select option: ").trim().toLowerCase(Locale.ROOT);
            try {
                switch (input) {
                    case "1" -> addPatronFlow();
                    case "2" -> updatePatronFlow();
                    case "3" -> listAllPatronsFlow();
                    case "4" -> patronHistoryFlow();
                    case "5" -> recommendBooksFlow();
                    case "6" -> myReservationsFlow();
                    case "7" -> myNotificationsFlow();
                    case "b", "back" -> {
                        return;
                    }
                    default -> System.out.println("Unknown option: " + input);
                }
            } catch (RuntimeException ex) {
                logger.warning(ex.getMessage());
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void lendingMenu() {
        while (true) {
            System.out.println();
            System.out.println("--- Lending ---");
            System.out.println("1) Checkout book");
            System.out.println("2) Return book");
            System.out.println("3) Reserve book (only if checked out)");
            System.out.println("4) List active loans (borrowed books)");
            System.out.println("5) List available books");
            System.out.println("b) Back");

            String input = readLine("Select option: ").trim().toLowerCase(Locale.ROOT);
            try {
                switch (input) {
                    case "1" -> checkoutFlow();
                    case "2" -> returnFlow();
                    case "3" -> reserveBookFlow();
                    case "4" -> listActiveLoansFlow();
                    case "5" -> listAvailableBooksFlow();
                    case "b", "back" -> {
                        return;
                    }
                    default -> System.out.println("Unknown option: " + input);
                }
            } catch (RuntimeException ex) {
                logger.warning(ex.getMessage());
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void addBookFlow() {
        System.out.println("ISBN must be 10 or 13 digits. Hyphens/spaces are allowed.");
        System.out.println("Example: 1234567890 or 978-0132350884");
        String isbn = readLine("ISBN (10 or 13 digits): ");
        String title = readLine("Title: ");
        String author = readLine("Author: ");
        int year = readInt("Publication year (e.g., 2008): ");

        bookService.addBook(new com.airtribe.librarymanagement.entity.Book(isbn, title, author, year));
        System.out.println("Book added.");
    }

    private void updateBookFlow() {
        System.out.println("ISBN must be 10 or 13 digits. Hyphens/spaces are allowed.");
        String isbn = readLine("ISBN to update (10/13 digits): ");
        String title = readLine("New title: ");
        String author = readLine("New author: ");
        int year = readInt("New publication year (e.g., 2011): ");

        bookService.updateBook(isbn, title, author, year);
        System.out.println("Book updated.");
    }

    private void removeBookFlow() {
        System.out.println("ISBN must be 10 or 13 digits. Hyphens/spaces are allowed.");
        String isbn = readLine("ISBN to remove (10/13 digits): ");
        bookService.removeBook(isbn);
        System.out.println("Book removed.");
    }

    private void searchBooksFlow() {
        System.out.println("Search by: 1) Title  2) Author  3) ISBN");
        String mode = readLine("Select: ").trim();
        String query = switch (mode) {
            case "1" -> readLine("Title contains: ");
            case "2" -> readLine("Author contains: ");
            case "3" -> readLine("ISBN (10/13 digits; hyphens ok): ");
            default -> readLine("Query: ");
        };

        java.util.List<com.airtribe.librarymanagement.entity.Book> results = switch (mode) {
            case "1" -> bookService.search(com.airtribe.librarymanagement.search.BookSearchType.TITLE, query);
            case "2" -> bookService.search(com.airtribe.librarymanagement.search.BookSearchType.AUTHOR, query);
            case "3" -> bookService.search(com.airtribe.librarymanagement.search.BookSearchType.ISBN, query);
            default -> throw new IllegalArgumentException("Invalid search mode: " + mode);
        };

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
            return;
        }
        System.out.println("Matches:");
        for (var b : results) {
            System.out.println(" - " + b);
        }
    }

    private void listAllBooksFlow() {
        var books = bookService.listAllBooks();
        if (books.isEmpty()) {
            System.out.println("No books in inventory.");
            return;
        }
        System.out.println("All books:");
        for (var b : books) {
            System.out.println(" - " + b);
        }
    }

    private void addPatronFlow() {
        System.out.println("Tip: Patron ID can be any unique value (example: P1, P1001, alice01).");
        String patronId = readLine("Patron ID (unique): ");
        String name = readLine("Name: ");
        System.out.println("Tip: Contact can be phone or email.");
        String contact = readLine("Contact (phone/email): ");
        patronService.addPatron(new com.airtribe.librarymanagement.entity.Patron(patronId, name, contact));
        System.out.println("Patron added.");
    }

    private void updatePatronFlow() {
        String patronId = readLine("Patron ID to update: ");
        String name = readLine("New name: ");
        String contact = readLine("New contact: ");
        patronService.updatePatron(patronId, name, contact);
        System.out.println("Patron updated.");
    }

    private void recommendBooksFlow() {
        String patronId = readLine("Patron ID: ");
        int limit = readInt("How many recommendations? ");
        var recs = recommendationService.recommendForPatron(patronId, limit);
        if (recs.isEmpty()) {
            System.out.println("No recommendations found right now.");
            return;
        }
        System.out.println("Recommended books (available, not previously borrowed):");
        for (var b : recs) {
            System.out.println(" - " + b);
        }
    }

    private void myReservationsFlow() {
        String patronId = readLine("Patron ID (example: P1): ");
        var reservations = reservationService.listReservationsForPatron(patronId);
        if (reservations.isEmpty()) {
            System.out.println("No active reservations.");
            return;
        }
        System.out.println("Your reservations:");
        for (var r : reservations) {
            System.out.println(" - " + r);
        }
    }

    private void myNotificationsFlow() {
        String patronId = readLine("Patron ID (example: P1): ");
        var notifications = notificationService.getNotifications(patronId);
        if (notifications.isEmpty()) {
            System.out.println("No notifications.");
            return;
        }
        System.out.println("Your notifications:");
        for (var n : notifications) {
            System.out.println(" - " + n.getCreatedAt() + " | " + n.getMessage());
        }
    }

    private void listAllPatronsFlow() {
        var patrons = patronService.listAllPatrons();
        if (patrons.isEmpty()) {
            System.out.println("No patrons registered.");
            return;
        }
        System.out.println("All patrons:");
        for (var p : patrons) {
            System.out.println(" - " + p);
        }
    }

    private void patronHistoryFlow() {
        String patronId = readLine("Patron ID (example: P1): ");
        var loans = patronService.getBorrowHistory(patronId);
        if (loans.isEmpty()) {
            System.out.println("No borrowing history for patron: " + patronId);
            return;
        }
        System.out.println("Borrowing history:");
        for (var l : loans) {
            System.out.println(" - " + l);
        }
    }

    private void checkoutFlow() {
        String patronId = readLine("Patron ID (example: P1): ");
        System.out.println("ISBN must be 10 or 13 digits. Hyphens/spaces are allowed.");
        String isbn = readLine("ISBN (10/13 digits): ");
        var loan = lendingService.checkout(patronId, isbn);
        System.out.println("Checked out. Due date: " + loan.getDueDate());
    }

    private void returnFlow() {
        String patronId = readLine("Patron ID (must match borrower): ");
        System.out.println("ISBN must be 10 or 13 digits. Hyphens/spaces are allowed.");
        String isbn = readLine("ISBN (10/13 digits): ");
        lendingService.returnBook(patronId, isbn);
        System.out.println("Returned.");
    }

    private void reserveBookFlow() {
        String patronId = readLine("Patron ID (example: P1): ");
        System.out.println("Reservation is allowed only if the book is currently checked out.");
        System.out.println("ISBN must be 10 or 13 digits. Hyphens/spaces are allowed.");
        String isbn = readLine("ISBN (10/13 digits): ");
        var reservation = reservationService.reserveBook(patronId, isbn);
        System.out.println("Reserved. Reservation ID: " + reservation.getReservationId());
        System.out.println("You will be notified when the book becomes available.");
    }

    private void listActiveLoansFlow() {
        var loans = lendingService.listAllActiveLoans();
        if (loans.isEmpty()) {
            System.out.println("No active loans.");
            return;
        }
        System.out.println("Active loans:");
        for (var l : loans) {
            System.out.println(" - " + l);
        }
    }

    private void listAvailableBooksFlow() {
        var allBooks = bookService.listAllBooks();
        var activeLoans = lendingService.listAllActiveLoans();
        java.util.Set<String> borrowedIsbns = new java.util.HashSet<>();
        for (var loan : activeLoans) {
            borrowedIsbns.add(loan.getIsbn());
        }

        java.util.List<com.airtribe.librarymanagement.entity.Book> available = new java.util.ArrayList<>();
        for (var book : allBooks) {
            if (!borrowedIsbns.contains(book.getIsbn())) {
                available.add(book);
            }
        }

        if (available.isEmpty()) {
            System.out.println("No available books.");
            return;
        }
        System.out.println("Available books:");
        for (var b : available) {
            System.out.println(" - " + b);
        }
    }

    private int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt).trim();
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

}


