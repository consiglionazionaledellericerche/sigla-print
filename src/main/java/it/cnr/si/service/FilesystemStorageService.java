package it.cnr.si.service;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by francesco on 26/06/17.
 */

@Service
@ConditionalOnProperty(name = "print.output.dir")
public class FilesystemStorageService implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesystemStorageService.class);

    @Value("${print.output.dir}")
    private String printOutputDir;

    @Override
    public void write(String id, byte[] byteArray) {

        String path = makePath(id);
        LOGGER.info("writing file {}", path);
        File output = new File(path);
        try {
            FileUtils.writeByteArrayToFile(output, byteArray);
        } catch (IOException e) {
            throw new RuntimeException("cannot write " + path, e);
        }
    }

    @Override
    public boolean delete(String id) {
        String path = makePath(id);
        LOGGER.info("deleting {}", path);
        File file = new File(path);
        return file.delete();
    }

    @Override
    public InputStream get(String id) {
        String path = makePath(id);
        LOGGER.info("get file {}", path);
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("file not found " + path, e);
        }
    }

    private final String makePath(String id) {
        return printOutputDir + File.separator + id;
    }

}
