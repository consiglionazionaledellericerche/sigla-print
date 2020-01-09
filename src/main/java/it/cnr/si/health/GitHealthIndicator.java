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

package it.cnr.si.health;

import it.cnr.si.config.JasperSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class GitHealthIndicator implements HealthIndicator {

    @Autowired
    private JasperSource jasperSource;

    @Override
    public Health health() {
        try {
            final String content = jasperSource.getContentAsString("README.md");

            if (content.startsWith("Sigla")) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("message", content).build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("message", e.getMessage()).build();
        }
    }
}
