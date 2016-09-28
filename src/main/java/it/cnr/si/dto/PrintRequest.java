package it.cnr.si.dto;

/**
 * Created by francesco on 09/09/16.
 */
public class PrintRequest {

    private String name;

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PrintRequest{" +
                "path='" + path + '\'' +
                ", name='" + name + '\'' +
                '}';
    }



}
