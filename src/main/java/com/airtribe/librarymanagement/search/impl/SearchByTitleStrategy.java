package com.airtribe.librarymanagement.search.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import com.airtribe.librarymanagement.entity.Book;
import com.airtribe.librarymanagement.search.BookSearchStrategy;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class SearchByTitleStrategy implements BookSearchStrategy {
    @Override
    public List<Book> search(Collection<Book> books, String query) {
        String q = ValidationUtils.requireNonBlank(query, "Title query").toLowerCase(Locale.ROOT);
        List<Book> matches = new ArrayList<>();
        for (Book b : books) {
            if (b.getTitle().toLowerCase(Locale.ROOT).contains(q)) {
                matches.add(b);
            }
        }
        return List.copyOf(matches);
    }
}


