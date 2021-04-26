package ru.otus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@EnableJdbcRepositories(basePackageClasses = {ru.otus.crm.repository.ClassForStartScan.class})
public class PersistenceConfig {

    @Bean(name = "demoDbDataSource")
    @ConfigurationProperties("app.datasource.demo-db")
    public DataSource demoDbDataSource() {
        return DataSourceBuilder.create().build();
    }
}
