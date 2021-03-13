package ru.otus.jdbc.mapper;

import ru.otus.core.dao.ClientDao;
import ru.otus.core.model.Client;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.jdbc.DbExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class JdbcMapperClient implements ClientDao {

    private final TransactionManager transactionManager;
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;

    public JdbcMapperClient(TransactionManager transactionManager, DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData) {
        this.transactionManager = transactionManager;
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<Client> findById(long id) {
        return Optional.empty();
    }

    @Override
    public List<Client> findAll() {
        return null;
    }

    @Override
    public long insert(Client client) {
        return 0;
    }

    @Override
    public void update(Client client) {

    }
}
