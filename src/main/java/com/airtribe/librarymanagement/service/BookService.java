package com.airtribe.librarymanagement.service;

import java.util.List;
import java.util.Objects;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import com.airtribe.librarymanagement.entity.Book;
import com.airtribe.librarymanagement.exception.NotFoundException;
import com.airtribe.librarymanagement.exception.RuleViolationException;
import com.airtribe.librarymanagement.repository.BookRepository;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.search.BookSearchStrategy;
import com.airtribe.librarymanagement.search.BookSearchType;
import com.airtribe.librarymanagement.search.impl.SearchByAuthorStrategy;
import com.airtribe.librarymanagement.search.impl.SearchByIsbnStrategy;
import com.airtribe.librarymanagement.search.impl.SearchByTitleStrategy;
import com.airtribe.librarymanagement.util.IsbnValidator;

public final class BookService {
    private static final Logger logger = Logger.getLogger(BookService.class.getName());

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final Map<BookSearchType, BookSearchStrategy> searchStrategies;

    public BookService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
        this.searchStrategies = defaultStrategies();
    }

    private static Map<BookSearchType, BookSearchStrategy> defaultStrategies() {
        Map<BookSearchType, BookSearchStrategy> map = new EnumMap<>(BookSearchType.class);
        map.put(BookSearchType.TITLE, new SearchByTitleStrategy());
        map.put(BookSearchType.AUTHOR, new SearchByAuthorStrategy());
        map.put(BookSearchType.ISBN, new SearchByIsbnStrategy());
        return map;
    }

    public void addBook(Book book) {
        Objects.requireNonNull(book, "book");
        if (bookRepository.existsByIsbn(book.getIsbn())) {
            throw new RuleViolationException("Book already exists with ISBN: " + book.getIsbn());
        }
        bookRepository.save(book);
        logger.info("Added book: " + book.getIsbn());
    }

    public void updateBook(String isbn, String title, String author, int publicationYear) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        Book book = bookRepository.findByIsbn(key).orElseThrow(() -> new NotFoundException("Book not found: " + key));
        book.updateMetadata(title, author, publicationYear);
        bookRepository.save(book);
        logger.info("Updated book: " + key);
    }

    public void removeBook(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        if (!bookRepository.existsByIsbn(key)) {
            throw new NotFoundException("Book not found: " + key);
        }
        if (loanRepository.findActiveLoanByIsbn(key).isPresent()) {
            throw new RuleViolationException("Cannot remove book; it is currently borrowed: " + key);
        }
        bookRepository.deleteByIsbn(key);
        logger.info("Removed book: " + key);
    }

    public Book getBookByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        return bookRepository.findByIsbn(key).orElseThrow(() -> new NotFoundException("Book not found: " + key));
    }

    public List<Book> listAllBooks() {
        return List.copyOf(bookRepository.findAll());
    }

    public List<Book> search(BookSearchType type, String query) {
        Objects.requireNonNull(type, "type");
        BookSearchStrategy strategy = searchStrategies.get(type);
        if (strategy == null) {
            throw new RuleViolationException("No search strategy registered for: " + type);
        }
        return strategy.search(bookRepository.findAll(), query);
    }

    public List<Book> searchByTitle(String titleQuery) {
        return search(BookSearchType.TITLE, titleQuery);
    }

    public List<Book> searchByAuthor(String authorQuery) {
        return search(BookSearchType.AUTHOR, authorQuery);
    }

    public List<Book> searchByIsbn(String isbn) {
        // Keep normalization behavior consistent with other entry points
        String key = IsbnValidator.validateAndNormalize(isbn);
        return search(BookSearchType.ISBN, key);
    }
}


