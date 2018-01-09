package it.cnr.si.health;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GitlabHealthIndicator implements HealthIndicator {

    private RestTemplate restTemplate = new RestTemplate();

    @Value("${cnr.gitlab.url}")
    private String gitlabUrl;

    @Value("${cnr.gitlab.token}")
    private String gitlabToken;

    @Override
    public Health health() {

        String content  = restTemplate.getForObject(gitlabUrl + "/README.md" + "?private_token={private_token}",
                String.class, gitlabToken);

        if (content.startsWith("Sigla")) {
            return Health.up().build();
        } else {
            return Health.down().withDetail("message", content).build();
        }

    }
}
