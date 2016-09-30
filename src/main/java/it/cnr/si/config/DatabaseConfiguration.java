package it.cnr.si.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by francesco on 12/09/16.
 */

@Configuration
public class DatabaseConfiguration {

    private DataSource dataSource;

    @Value("${spring.jpa.properties.hibernate.connection.autocommit}")
    private Boolean autocommit;
    
    public DatabaseConfiguration (DataSource dataSource) {
        this.dataSource = dataSource; 
    }

    @Bean
    public Connection connection () throws SQLException {
    	Connection conn = dataSource.getConnection();
    	conn.setAutoCommit(autocommit);
        return conn;
    }

}
