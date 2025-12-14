package com.airtribe.librarymanagement.repository;

import com.airtribe.librarymanagement.entity.Notification;

import java.util.List;

public interface NotificationRepository {
    void save(Notification notification);

    List<Notification> findByPatronId(String patronId);
}


