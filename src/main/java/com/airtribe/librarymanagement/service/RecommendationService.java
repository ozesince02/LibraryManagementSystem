package com.airtribe.librarymanagement.service;

import com.airtribe.librarymanagement.entity.Book;
import com.airtribe.librarymanagement.entity.Loan;
import com.airtribe.librarymanagement.exception.NotFoundException;
import com.airtribe.librarymanagement.repository.BookRepository;
import com.airtribe.librarymanagement.repository.LoanRepository;
import com.airtribe.librarymanagement.repository.PatronRepository;
import com.airtribe.librarymanagement.util.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Recommendation System (simple + efficient):
 * - Uses patron borrowing history (implicit preferences)
 * - Builds frequency maps and returns top-N using a min-heap (priority queue)
 */
public final class RecommendationService {
    private final BookRepository bookRepository;
    private final PatronRepository patronRepository;
    private final LoanRepository loanRepository;

    public RecommendationService(BookRepository bookRepository, PatronRepository patronRepository, LoanRepository loanRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.patronRepository = Objects.requireNonNull(patronRepository, "patronRepository");
        this.loanRepository = Objects.requireNonNull(loanRepository, "loanRepository");
    }

    public List<Book> recommendForPatron(String patronId, int limit) {
        String patronKey = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        if (limit <= 0) {
            return List.of();
        }

        if (!patronRepository.existsById(patronKey)) {
            throw new NotFoundException("Patron not found: " + patronKey);
        }

        List<Loan> history = loanRepository.findLoansByPatronId(patronKey);
        Set<String> alreadyBorrowedIsbns = new HashSet<>();
        Map<String, Integer> authorFrequency = new HashMap<>();
        Map<String, Integer> titleTokenFrequency = new HashMap<>();

        for (Loan loan : history) {
            alreadyBorrowedIsbns.add(loan.getIsbn());
            bookRepository.findByIsbn(loan.getIsbn()).ifPresent(book -> {
                String author = normalize(book.getAuthor());
                if (!author.isEmpty()) {
                    authorFrequency.merge(author, 1, Integer::sum);
                }
                for (String token : tokenizeTitle(book.getTitle())) {
                    titleTokenFrequency.merge(token, 1, Integer::sum);
                }
            });
        }

        PriorityQueue<ScoredBook> top = new PriorityQueue<>(); // min-heap by score

        for (Book candidate : bookRepository.findAll()) {
            String isbn = candidate.getIsbn();

            // Recommend only available books
            if (loanRepository.findActiveLoanByIsbn(isbn).isPresent()) {
                continue;
            }

            // Avoid recommending a book already borrowed in the past (simple UX choice)
            if (alreadyBorrowedIsbns.contains(isbn)) {
                continue;
            }

            int score = score(candidate, authorFrequency, titleTokenFrequency);
            if (score <= 0) {
                continue;
            }

            top.offer(new ScoredBook(candidate, score));
            if (top.size() > limit) {
                top.poll(); // keep only top-N
            }
        }

        List<ScoredBook> sorted = new ArrayList<>(top);
        sorted.sort((a, b) -> {
            int byScore = Integer.compare(b.score, a.score);
            if (byScore != 0) return byScore;
            return a.book.getTitle().compareToIgnoreCase(b.book.getTitle());
        });

        List<Book> result = new ArrayList<>(sorted.size());
        for (ScoredBook sb : sorted) {
            result.add(sb.book);
        }
        return List.copyOf(result);
    }

    private static int score(
            Book book,
            Map<String, Integer> authorFrequency,
            Map<String, Integer> titleTokenFrequency
    ) {
        int score = 0;

        String author = normalize(book.getAuthor());
        if (!author.isEmpty()) {
            Integer count = authorFrequency.get(author);
            if (count != null) {
                score += Math.min(6, count * 2); // history boost (capped)
            }
        }

        for (String token : tokenizeTitle(book.getTitle())) {
            Integer tokenCount = titleTokenFrequency.get(token);
            if (tokenCount != null) {
                score += Math.min(3, tokenCount); // small title similarity boost (capped)
            }
        }

        return score;
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private static List<String> tokenizeTitle(String title) {
        if (title == null) return List.of();
        String[] parts = title.toLowerCase(Locale.ROOT).split("[^a-z0-9]+");
        List<String> tokens = new ArrayList<>();
        for (String p : parts) {
            if (p.length() >= 3) {
                tokens.add(p);
            }
        }
        return tokens;
    }

    private static final class ScoredBook implements Comparable<ScoredBook> {
        private final Book book;
        private final int score;

        private ScoredBook(Book book, int score) {
            this.book = book;
            this.score = score;
        }

        @Override
        public int compareTo(ScoredBook other) {
            int byScore = Integer.compare(this.score, other.score); // min-heap
            if (byScore != 0) return byScore;
            return this.book.getTitle().compareToIgnoreCase(other.book.getTitle());
        }
    }
}


