package it.cnr.si.service;

import it.cnr.rsi.storage.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

/**
 * Created by francesco on 23/06/17.
 */

@Service
public class PrintStorageService  {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintStorageService.class);

    private StorageService storageService;

    public PrintStorageService(StorageService storageService) {
        LOGGER.info("using {}", storageService.getClass().getSimpleName());
        this.storageService = storageService;
    }

    public CompletableFuture<Void> write(String id, byte[] byteArray) {
        LOGGER.info("writing {} bytes to {}", byteArray.length, id);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray);
        return storageService
                .createAsync(inputStream, id);
    }

    public boolean delete(String id) {
        LOGGER.info("deleting {}", id);
        Boolean deleted = storageService.deleteAsync(id).join();
        LOGGER.info("{} deleted = {}", id, deleted);
        return deleted;
    }

    public InputStream get(String id) {
        LOGGER.info("get {}", id);
        return storageService.getInputStream(id);
    }
}
