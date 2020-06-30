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

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@ConditionalOnProperty({"cnr.gitlab.url"})
@EnableConfigurationProperties({GitLabConfigurationProperties.class})
public class GitLabConfiguration {
    @Autowired
    private GitLabConfigurationProperties gitLabConfigurationProperties;

    @Bean
    public GitLabApi createGitLabApi() {
        return new GitLabApi(GitLabApi.ApiVersion.V4, gitLabConfigurationProperties.getUrl(), gitLabConfigurationProperties.getToken());
    }

    @Bean
    public Project createProject() throws GitLabApiException {
        return createGitLabApi().getProjectApi().getProject(getGroup(), getProject());
    }

    @Bean
    public JasperSource createJasperSource(GitLabApi gitLabApi, GitLabConfiguration gitLabConfiguration, Project project) {
        return new JasperSource() {
            @Override
            public String getContentAsString(String key) throws Exception {
                return gitLabApi.getRepositoryFileApi().getFile(
                        project,
                        Optional.ofNullable(key)
                            .filter(s -> s.startsWith("/"))
                            .map(s -> s.substring(1))
                            .orElse(key),
                        gitLabConfiguration.getRef()
                ).getDecodedContentAsString();
            }

            @Override
            public byte[] getContentAsBytes(String key) throws Exception {
                return gitLabApi.getRepositoryFileApi().getFile(
                        project,
                        Optional.ofNullable(key)
                                .filter(s -> s.startsWith("/"))
                                .map(s -> s.substring(1))
                                .orElse(key),
                        gitLabConfiguration.getRef()
                ).getDecodedContentAsBytes();
            }
        };
    }

    public String getGroup() {
        return gitLabConfigurationProperties.getGroup();
    }

    public String getProject() {
        return gitLabConfigurationProperties.getProject();
    }

    public String getRef() {
        return gitLabConfigurationProperties.getRef();
    }
}
