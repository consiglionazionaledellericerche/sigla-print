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

import com.jcabi.github.Coordinates;
import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.RtGithub;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Configuration
@ConditionalOnProperty({"cnr.github.repo"})
@EnableConfigurationProperties({GitHubConfigurationProperties.class})
public class GitHubConfiguration {
    @Autowired
    private GitHubConfigurationProperties gitHubConfigurationProperties;

    @Bean
    public Github createGithub() {
        return new RtGithub();
    }

    @Bean
    public Repo createRepo(Github github) {
        return github.repos().get(
                new Coordinates.Simple(gitHubConfigurationProperties.getRepo())
        );
    }

    @Bean
    public JasperSource createJasperSource(Repo repo) {
        return new JasperSource() {
            @Override
            public String getContentAsString(String key) throws Exception {
                return IOUtils.toString(repo.contents().get(key,
                        Optional.ofNullable(gitHubConfigurationProperties.getBranch()).orElse("master")
                ).raw(), StandardCharsets.UTF_8.name());
            }

            @Override
            public byte[] getContentAsBytes(String key) throws Exception {
                return IOUtils.toByteArray(repo.contents().get(key,
                        Optional.ofNullable(gitHubConfigurationProperties.getBranch()).orElse("master")
                ).raw());
            }
        };
    }

}
