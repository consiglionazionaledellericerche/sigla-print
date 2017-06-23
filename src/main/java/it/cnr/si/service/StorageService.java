package it.cnr.si.service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by francesco on 23/06/17.
 */
public interface StorageService {

    void write(String id, OutputStream outputStream);

    void delete(String id);

    InputStream get(String id);
}
