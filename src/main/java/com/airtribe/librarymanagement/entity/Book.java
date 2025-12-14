package com.airtribe.librarymanagement.entity;

import java.util.Objects;

import com.airtribe.librarymanagement.util.IsbnValidator;
import com.airtribe.librarymanagement.util.ValidationUtils;

public final class Book {
    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = IsbnValidator.validateAndNormalize(isbn);
        this.title = ValidationUtils.requireNonBlank(title, "Title");
        this.author = ValidationUtils.requireNonBlank(author, "Author");
        this.publicationYear = ValidationUtils.requirePositive(publicationYear, "Publication year");
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void updateMetadata(String title, String author, int publicationYear) {
        this.title = ValidationUtils.requireNonBlank(title, "Title");
        this.author = ValidationUtils.requireNonBlank(author, "Author");
        this.publicationYear = ValidationUtils.requirePositive(publicationYear, "Publication year");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                '}';
    }
}


