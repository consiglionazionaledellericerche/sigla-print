package it.cnr.si.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by francesco on 12/09/16.
 */

@Configuration
public class DatabaseConfiguration {

    private DataSource dataSource;

    public DatabaseConfiguration (DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Connection connection () throws SQLException {
        return dataSource.getConnection();
    }

}
