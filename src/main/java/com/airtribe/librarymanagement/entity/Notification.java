package com.airtribe.librarymanagement.entity;

import com.airtribe.librarymanagement.util.ValidationUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public final class Notification {
    private final String notificationId; // identity
    private final String patronId;
    private final String message;
    private final LocalDateTime createdAt;
    private boolean read;

    public Notification(String notificationId, String patronId, String message, LocalDateTime createdAt) {
        this.notificationId = ValidationUtils.requireNonBlank(notificationId, "Notification ID");
        this.patronId = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        this.message = ValidationUtils.requireNonBlank(message, "Message");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
        this.read = false;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getPatronId() {
        return patronId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }

    public void markRead() {
        this.read = true;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", patronId='" + patronId + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                ", read=" + read +
                '}';
    }
}


