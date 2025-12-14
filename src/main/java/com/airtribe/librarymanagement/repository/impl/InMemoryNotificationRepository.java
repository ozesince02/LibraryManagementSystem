package com.airtribe.librarymanagement.repository.impl;

import com.airtribe.librarymanagement.entity.Notification;
import com.airtribe.librarymanagement.repository.NotificationRepository;
import com.airtribe.librarymanagement.util.ValidationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InMemoryNotificationRepository implements NotificationRepository {
    private final Map<String, List<Notification>> byPatronId = new HashMap<>();

    @Override
    public void save(Notification notification) {
        byPatronId.computeIfAbsent(notification.getPatronId(), ignored -> new ArrayList<>()).add(notification);
    }

    @Override
    public List<Notification> findByPatronId(String patronId) {
        String key = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        return Collections.unmodifiableList(byPatronId.getOrDefault(key, List.of()));
    }
}


