package com.airtribe.librarymanagement.repository;

import com.airtribe.librarymanagement.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    void enqueue(Reservation reservation);

    Optional<Reservation> peekNextByIsbn(String isbn);

    Optional<Reservation> dequeueNextByIsbn(String isbn);

    boolean hasReservations(String isbn);

    boolean hasReservation(String isbn, String patronId);

    List<Reservation> findReservationsByPatronId(String patronId);
}


