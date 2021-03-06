/*
 * Copyright (C) 2020  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.si.config;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.jpa.properties.hibernate.connection.autocommit}")
    private Boolean autocommit;

    public DatabaseConfiguration(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection connection() throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.setAutoCommit(autocommit);
        return conn;
    }
}
