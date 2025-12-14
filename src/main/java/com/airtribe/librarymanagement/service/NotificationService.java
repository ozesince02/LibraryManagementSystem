package com.airtribe.librarymanagement.service;

import com.airtribe.librarymanagement.entity.Notification;
import com.airtribe.librarymanagement.repository.NotificationRepository;
import com.airtribe.librarymanagement.util.IdGenerator;
import com.airtribe.librarymanagement.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public final class NotificationService {
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = Objects.requireNonNull(notificationRepository, "notificationRepository");
    }

    public void notifyPatron(String patronId, String message) {
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        String msg = ValidationUtils.requireNonBlank(message, "Message");

        Notification notification = new Notification(IdGenerator.newId(), p, msg, LocalDateTime.now());
        notificationRepository.save(notification);
        logger.info("Notification created for patron " + p + ": " + msg);
    }

    public List<Notification> getNotifications(String patronId) {
        String p = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        return notificationRepository.findByPatronId(p);
    }
}


