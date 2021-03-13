package ru.otus;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.dao.ClientDao;
import ru.otus.core.model.Client;
import ru.otus.core.service.DbServiceClientImpl;
import ru.otus.demo.DataSourceDemo;
import ru.otus.jdbc.DbExecutorImpl;
import ru.otus.jdbc.dao.ClientDaoJdbc;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;
import ru.otus.jdbc.mapper.JdbcMapperClient;
import ru.otus.jdbc.sessionmanager.TransactionManagerJdbc;

import javax.sql.DataSource;

public class HomeWork {
    private static final Logger log = LoggerFactory.getLogger(HomeWork.class);

    public static void main(String[] args) {
// Общая часть
        var dataSource = new DataSourceDemo();
        flywayMigrations(dataSource);
        var transactionManager = new TransactionManagerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();

// Работа с клиентом
        EntityClassMetaData entityClassMetaDataClient; // = new EntityClassMetaDataImpl();
        EntitySQLMetaData entitySQLMetaDataClient = null; //= new EntitySQLMetaDataImpl();
        ClientDao clientDao = new JdbcMapperClient(transactionManager, dbExecutor, entitySQLMetaDataClient);

// Код дальше должен остаться, т.е. clientDao должен использоваться
        var dbServiceClient = new DbServiceClientImpl(clientDao);
        dbServiceClient.saveClient(new Client("dbServiceFirst"));

        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var clientSecondSelected = dbServiceClient.getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        log.info("clientSecondSelected:{}", clientSecondSelected);

// Работа со счетом


    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
