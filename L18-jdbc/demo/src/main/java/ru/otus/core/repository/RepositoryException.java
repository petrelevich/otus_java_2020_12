package ru.otus.core.repository;

public class RepositoryException extends RuntimeException {
    public RepositoryException(Exception ex) {
        super(ex);
    }
}
