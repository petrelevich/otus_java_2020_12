package ru.otus.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.dao.ClientDao;
import ru.otus.core.model.Client;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final ClientDao clientDao;

    public DbServiceClientImpl(ClientDao clientDao) {
        this.clientDao = clientDao;
    }

    @Override
    public Client saveClient(Client client) {
        if (client.getId() == null) {
            var clientId = clientDao.insert(client);
            var createdClient = new Client(clientId, client.getName());
            log.info("created client: {}", createdClient);
            return createdClient;
        }
        clientDao.update(client);
        log.info("updated client: {}", client);
        return client;
    }

    @Override
    public Optional<Client> getClient(long id) {
        var clientOptional = clientDao.findById(id);
        log.info("client: {}", clientOptional);
        return clientOptional;
    }

    @Override
    public List<Client> findAll() {
        var clientList = clientDao.findAll();
        log.info("clientList:{}", clientList);
        return clientList;
    }
}
