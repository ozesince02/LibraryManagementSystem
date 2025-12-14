package com.airtribe.librarymanagement.search.impl;

import java.util.Collection;
import java.util.List;

import com.airtribe.librarymanagement.entity.Book;
import com.airtribe.librarymanagement.search.BookSearchStrategy;
import com.airtribe.librarymanagement.util.IsbnValidator;

public final class SearchByIsbnStrategy implements BookSearchStrategy {
    @Override
    public List<Book> search(Collection<Book> books, String query) {
        String isbn = IsbnValidator.validateAndNormalize(query);
        for (Book b : books) {
            if (b.getIsbn().equals(isbn)) {
                return List.of(b);
            }
        }
        return List.of();
    }
}


