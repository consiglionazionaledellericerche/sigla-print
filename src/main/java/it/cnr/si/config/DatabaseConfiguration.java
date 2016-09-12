package it.cnr.si.config;

import oracle.jdbc.pool.OracleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Created by francesco on 12/09/16.
 */

@Configuration
public class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);
    public static final String JDBC_URL = "jdbc:oracle:thin:@dbtest.cedrc.cnr.it:1521:SIGLAF";

    @Bean
    public DataSource dataSource() throws SQLException {

        LOGGER.info("connecting to {}", JDBC_URL);

        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser("PCIR009");
        dataSource.setPassword("dbform");
        dataSource.setURL(JDBC_URL);
        dataSource.setImplicitCachingEnabled(true);
        dataSource.setFastConnectionFailoverEnabled(true);
        return dataSource;
    }



}
