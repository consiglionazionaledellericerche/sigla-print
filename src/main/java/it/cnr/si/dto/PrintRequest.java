package it.cnr.si.dto;

/**
 * Created by francesco on 09/09/16.
 */
public class PrintRequest {
    private long id;

    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
