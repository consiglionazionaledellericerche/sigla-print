package it.cnr.si.dto;

import java.util.List;

/**
 * Created by francesco on 26/09/16.
 */
public class Commit {

    private String id;
    private String message;
    private List<String> added;
    private List<String> modified;
    private List<String> removed;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getAdded() {
        return added;
    }

    public void setAdded(List<String> added) {
        this.added = added;
    }

    public List<String> getModified() {
        return modified;
    }

    public void setModified(List<String> modified) {
        this.modified = modified;
    }

    public List<String> getRemoved() {
        return removed;
    }

    public void setRemoved(List<String> removed) {
        this.removed = removed;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", added=" + added +
                ", modified=" + modified +
                ", removed=" + removed +
                '}';
    }
}
