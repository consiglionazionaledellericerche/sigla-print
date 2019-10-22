package it.cnr.si.health;

import it.cnr.si.config.GitLabConfiguration;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitlabHealthIndicator implements HealthIndicator {

    @Autowired
    private GitLabApi gitLabApi;

    @Autowired
    private GitLabConfiguration gitLabConfiguration;

    @Autowired
    private Project project;

    @Override
    public Health health() {
        try {
            final RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(project, "README.md", gitLabConfiguration.getRef());
            final String content = file.getDecodedContentAsString();

            if (content.startsWith("Sigla")) {
                return Health.up().build();
            } else {
                return Health.down().withDetail("message", content).build();
            }
        } catch (GitLabApiException e) {
            return Health.down().withDetail("message", e.getMessage()).build();
        }
    }
}
