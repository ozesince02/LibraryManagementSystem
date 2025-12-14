package com.airtribe.librarymanagement.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.airtribe.librarymanagement.entity.Patron;
import com.airtribe.librarymanagement.repository.PatronRepository;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class InMemoryPatronRepository implements PatronRepository {
    private final Map<String, Patron> byId = new HashMap<>();

    @Override
    public void save(Patron patron) {
        byId.put(patron.getPatronId(), patron);
    }

    @Override
    public Optional<Patron> findById(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        return Optional.ofNullable(byId.get(key));
    }

    @Override
    public boolean existsById(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        return byId.containsKey(key);
    }

    @Override
    public void deleteById(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        byId.remove(key);
    }

    @Override
    public Collection<Patron> findAll() {
        return Collections.unmodifiableCollection(byId.values());
    }
}


