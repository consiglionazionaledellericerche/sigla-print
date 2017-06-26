package it.cnr.si.service;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Created by francesco on 23/06/17.
 */

@Service
@ConditionalOnProperty("azure.foo.bar")
public class AzureStorageService implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureStorageService.class);

    @Override
    public void write(String id, byte[] byteArray) {
        throw new NotImplementedException("cannot write " + id);

    }

    @Override
    public boolean delete(String id) {
        throw new NotImplementedException("cannot delete " + id);
    }

    @Override
    public InputStream get(String id) {
        throw new NotImplementedException("cannot get " + id);
    }
}
