package ru.otus.core.sessionmanager;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class TransactionManagerJdbc implements TransactionManager {
    private final DataSource dataSource;

    public TransactionManagerJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T doInTransaction(TransactionAction<T> action) {
        return notTrowAction(() -> {
            try (var connection = dataSource.getConnection()) {
                try {
                    var result = action.apply(connection);
                    connection.commit();
                    return result;
                } catch (SQLException ex) {
                    connection.rollback();
                    throw new DataBaseOperationException("doInTransaction exception", ex);
                }
            }
        });
    }

    private <T> T notTrowAction(Callable<T> action) {
        try {
            return action.call();
        } catch (Exception ex) {
            throw new DataBaseOperationException("exception", ex);
        }
    }
}
