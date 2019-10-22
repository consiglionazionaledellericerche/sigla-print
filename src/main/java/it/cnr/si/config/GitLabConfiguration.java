package it.cnr.si.config;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
