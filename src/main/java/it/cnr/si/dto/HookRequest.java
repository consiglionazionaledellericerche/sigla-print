package it.cnr.si.dto;

import java.util.List;

/**
 * Created by francesco on 26/09/16.
 */
public class HookRequest {

    private List<Commit> commits;

    private String user_name;

    public List<Commit> getCommits() {
        return commits;
    }

    public void setCommits(List<Commit> commits) {
        this.commits = commits;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    @Override
    public String toString() {
        return "HookRequest{" +
                "commits=" + commits +
                ", user_name='" + user_name + '\'' +
                '}';
    }
}
