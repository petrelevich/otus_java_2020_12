package ru.otus.core.dao;

import ru.otus.core.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientDao {
    Optional<Client> findById(long id);

    List<Client> findAll();

    long insert(Client client);

    void update(Client client);
}
