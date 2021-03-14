package ru.otus.core.sessionmanager;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TransactionManagerJdbc implements TransactionManager {
    private final DataSource dataSource;

    public TransactionManagerJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T doInTransaction(TransactionAction<T> action) {
        try (var connection = dataSource.getConnection()) {
            var result = action.apply(connection);
            connection.commit();
            return result;
        } catch (SQLException ex) {
            throw new DataBaseOperationException("doInTransaction exception", ex);
        }
    }
}
