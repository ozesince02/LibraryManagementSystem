package com.airtribe.librarymanagement.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.airtribe.librarymanagement.entity.Book;
import com.airtribe.librarymanagement.repository.BookRepository;
import com.airtribe.librarymanagement.util.IsbnValidator;

public final class InMemoryBookRepository implements BookRepository {
    private final Map<String, Book> byIsbn = new HashMap<>();

    @Override
    public void save(Book book) {
        byIsbn.put(book.getIsbn(), book);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        return Optional.ofNullable(byIsbn.get(key));
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        return byIsbn.containsKey(key);
    }

    @Override
    public void deleteByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        byIsbn.remove(key);
    }

    @Override
    public Collection<Book> findAll() {
        return Collections.unmodifiableCollection(byIsbn.values());
    }
}


