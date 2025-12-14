package com.airtribe.librarymanagement.search;

import java.util.Collection;
import java.util.List;

import com.airtribe.librarymanagement.entity.Book;

public interface BookSearchStrategy {
    List<Book> search(Collection<Book> books, String query);
}


