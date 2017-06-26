package it.cnr.si.service;

import java.io.InputStream;

/**
 * Created by francesco on 23/06/17.
 */
public interface StorageService {

    void write(String id, byte[] byteArray);

    boolean delete(String id);

    InputStream get(String id);
}
