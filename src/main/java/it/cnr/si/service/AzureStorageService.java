package it.cnr.si.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by francesco on 23/06/17.
 */

@Service
public class AzureStorageService implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureStorageService.class);


    @Override
    public void write(String id, OutputStream outputStream) {

    }

    @Override
    public void delete(String id) {

    }

    @Override
    public InputStream get(String id) {
        return null;
    }
}
