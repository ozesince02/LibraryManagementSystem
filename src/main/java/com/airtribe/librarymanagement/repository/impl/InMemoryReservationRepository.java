package com.airtribe.librarymanagement.repository.impl;

import com.airtribe.librarymanagement.entity.Reservation;
import com.airtribe.librarymanagement.repository.ReservationRepository;
import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class InMemoryReservationRepository implements ReservationRepository {
    private final Map<String, Deque<Reservation>> queueByIsbn = new HashMap<>();

    @Override
    public void enqueue(Reservation reservation) {
        queueByIsbn.computeIfAbsent(reservation.getIsbn(), ignored -> new ArrayDeque<>()).addLast(reservation);
    }

    @Override
    public Optional<Reservation> peekNextByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        Deque<Reservation> q = queueByIsbn.get(key);
        if (q == null || q.isEmpty()) return Optional.empty();
        return Optional.ofNullable(q.peekFirst());
    }

    @Override
    public Optional<Reservation> dequeueNextByIsbn(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        Deque<Reservation> q = queueByIsbn.get(key);
        if (q == null || q.isEmpty()) return Optional.empty();
        Reservation r = q.pollFirst();
        if (q.isEmpty()) {
            queueByIsbn.remove(key);
        }
        return Optional.ofNullable(r);
    }

    @Override
    public boolean hasReservations(String isbn) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        Deque<Reservation> q = queueByIsbn.get(key);
        return q != null && !q.isEmpty();
    }

    @Override
    public boolean hasReservation(String isbn, String patronId) {
        String key = IsbnValidator.validateAndNormalize(isbn);
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        Deque<Reservation> q = queueByIsbn.get(key);
        if (q == null) return false;
        for (Reservation r : q) {
            if (r.getPatronId().equals(p)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Reservation> findReservationsByPatronId(String patronId) {
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        List<Reservation> out = new ArrayList<>();
        for (Deque<Reservation> q : queueByIsbn.values()) {
            for (Reservation r : q) {
                if (r.getPatronId().equals(p)) {
                    out.add(r);
                }
            }
        }
        return List.copyOf(out);
    }
}


