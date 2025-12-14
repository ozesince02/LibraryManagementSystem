package com.airtribe.librarymanagement.entity;

import java.util.Objects;

import com.airtribe.librarymanagement.util.ValidationUtils;

public final class Patron {
    private final String patronId;
    private String name;
    private String contact;

    public Patron(String patronId, String name, String contact) {
        this.patronId = ValidationUtils.requireNonBlank(patronId, "Patron ID");
        this.name = ValidationUtils.requireNonBlank(name, "Name");
        this.contact = ValidationUtils.requireNonBlank(contact, "Contact");
    }

    public String getPatronId() {
        return patronId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public void updateInfo(String name, String contact) {
        this.name = ValidationUtils.requireNonBlank(name, "Name");
        this.contact = ValidationUtils.requireNonBlank(contact, "Contact");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Patron patron)) return false;
        return Objects.equals(patronId, patron.patronId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patronId);
    }

    @Override
    public String toString() {
        return "Patron{" +
                "patronId='" + patronId + '\'' +
                ", name='" + name + '\'' +
                ", contact='" + contact + '\'' +
                '}';
    }
}


