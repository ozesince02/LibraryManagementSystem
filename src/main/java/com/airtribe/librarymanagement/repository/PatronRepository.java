package com.airtribe.librarymanagement.repository;

import java.util.Collection;
import java.util.Optional;

import com.airtribe.librarymanagement.entity.Patron;

public interface PatronRepository {
    void save(Patron patron);

    Optional<Patron> findById(String patronId);

    boolean existsById(String patronId);

    void deleteById(String patronId);

    Collection<Patron> findAll();
}


