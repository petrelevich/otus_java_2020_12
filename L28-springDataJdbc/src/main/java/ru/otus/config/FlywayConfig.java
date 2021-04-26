package ru.otus.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class FlywayConfig {
    private static final Logger log = LoggerFactory.getLogger(FlywayConfig.class);

    public FlywayConfig(@Qualifier("demoDbDataSource") DataSource demoDbDataSource) {
        migrateFlyway(demoDbDataSource);
    }

    public void migrateFlyway(DataSource demoDbDataSource) {
        log.info("Flyway migration started");
        var flyway = Flyway.configure()
                .dataSource(demoDbDataSource)
                .load();
        flyway.migrate();
        log.info("Flyway migration ended");
    }
}
