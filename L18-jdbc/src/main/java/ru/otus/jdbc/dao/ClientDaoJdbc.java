package ru.otus.jdbc.dao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.dao.ClientDao;
import ru.otus.core.dao.ClientDaoException;
import ru.otus.core.model.Client;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.jdbc.DbExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

// этот класс не должен быть в домашней работе
public class ClientDaoJdbc implements ClientDao {

    private final TransactionManager transactionManager;
    private final DbExecutor dbExecutor;

    public ClientDaoJdbc(TransactionManager transactionManager, DbExecutor dbExecutor) {
        this.transactionManager = transactionManager;
        this.dbExecutor = dbExecutor;
    }

    @Override
    public Optional<Client> findById(long id) {
        return transactionManager.doInTransaction(connection ->
                dbExecutor.executeSelect(connection, "select id, name from client where id  = ?", List.of(id), rs -> {
                    try {
                        if (rs.next()) {
                            return new Client(rs.getLong("id"), rs.getString("name"));
                        }
                        return null;
                    } catch (SQLException e) {
                        throw new ClientDaoException(e);
                    }
                }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInTransaction(connection ->
                dbExecutor.executeSelect(connection, "select * from client", Collections.emptyList(), rs -> {
                    var clientList = new ArrayList<Client>();
                    try {
                        while (rs.next()) {
                            clientList.add(new Client(rs.getLong("id"), rs.getString("name")));
                        }
                        return clientList;
                    } catch (SQLException e) {
                        throw new ClientDaoException(e);
                    }
                })).orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Client client) {
        try {
            return transactionManager.doInTransaction(connection ->
                    dbExecutor.executeStatement(connection, "insert into client(name) values (?)",
                            Collections.singletonList(client.getName()))
            );
        } catch (Exception e) {
            throw new ClientDaoException(e);
        }
    }

    @Override
    public void update(Client client) {
        try {
            transactionManager.doInTransaction(connection ->
                    dbExecutor.executeStatement(connection, "update client set name = ? where id = ?",
                            List.of(client.getName(), client.getId()))
            );
        } catch (Exception e) {
            throw new ClientDaoException(e);
        }
    }
}
