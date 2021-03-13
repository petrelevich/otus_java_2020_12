package ru.otus.core.dao;

import ru.otus.core.model.Client;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public interface ClientDao {
    Optional<Client> findById(Connection connection, long id);

    List<Client> findAll(Connection connection);

    long insert(Connection connection,Client client);

    void update(Connection connection,Client client);
}
