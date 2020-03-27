package it.cnr.si.dto;

import com.hazelcast.org.snakeyaml.engine.v1.api.DumpSettings;
import com.hazelcast.org.snakeyaml.engine.v1.api.lowlevel.Serialize;
import com.hazelcast.spi.serialization.SerializationService;

import java.io.Serializable;

public class EventPrintDsJson implements Serializable {

    private Long pg_stampa;

    private String json;

    public EventPrintDsJson(Long pg_stampa, String json) {
        this.json = json;
        this.pg_stampa=pg_stampa;
    }

    public String getJson() {
        return json;
    }

    public Long getPg_stampa() {
        return pg_stampa;
    }
}
