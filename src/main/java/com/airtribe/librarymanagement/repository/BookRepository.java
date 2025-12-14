package com.airtribe.librarymanagement.repository;

import java.util.Collection;
import java.util.Optional;

import com.airtribe.librarymanagement.entity.Book;

public interface BookRepository {
    void save(Book book);

    Optional<Book> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    void deleteByIsbn(String isbn);

    Collection<Book> findAll();
}


