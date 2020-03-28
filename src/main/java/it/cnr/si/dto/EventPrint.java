package it.cnr.si.dto;

import java.io.Serializable;

public class EventPrint implements Serializable {

    private Long pg_stampa;

    private String json;

    private String priotita;

    private Boolean isDsOnBody;

    public EventPrint(String priotita, Long pg_stampa, String json,boolean isDsOnBody) {
        this.priotita=priotita;
        this.json = json;
        this.pg_stampa=pg_stampa;
        this.isDsOnBody = isDsOnBody;
    }

    public String getPriotita() {
        return priotita;
    }

    public void setPriotita(String priotita) {
        this.priotita = priotita;
    }

    public String getJson() {
        return json;
    }

    public Long getPg_stampa() {
        return pg_stampa;
    }

    public Boolean getDsDsOnBody() {
        return isDsOnBody;
    }
}
