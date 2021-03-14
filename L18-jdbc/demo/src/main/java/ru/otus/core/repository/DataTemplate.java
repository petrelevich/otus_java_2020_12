package ru.otus.core.repository;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface DataTemplate<T> {
    Optional<T> findById(Connection connection, long id);

    List<T> findAll(Connection connection);

    long insert(Connection connection, T client);

    void update(Connection connection, T client);
}
