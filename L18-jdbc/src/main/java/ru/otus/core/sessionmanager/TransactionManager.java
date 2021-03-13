package ru.otus.core.sessionmanager;

import ru.otus.jdbc.sessionmanager.TransactionAction;

public interface TransactionManager {

    <T> T doInTransaction(TransactionAction<T> action);
}
