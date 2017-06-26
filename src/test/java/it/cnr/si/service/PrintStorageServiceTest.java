package it.cnr.si.service;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by francesco on 26/06/17.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintStorageServiceTest {

    public static final String FILE = "file";
    public static final String CONTENT = "content";
    public static final String NEW_FILE = "new-file";
    public static final String NEW_CONTENT = "foobar";

    @Autowired
    private PrintStorageService printStorageService;

    @Before
    public void before() {
        printStorageService.write(FILE, CONTENT.getBytes());
    }

    @After
    public void after() {
        printStorageService.delete(FILE);
    }

    @Test
    public void write() throws Exception {
        printStorageService.write(NEW_FILE, NEW_CONTENT.getBytes());
        assertEquals(NEW_CONTENT, IOUtils.toString(printStorageService.get(NEW_FILE), Charset.defaultCharset()));
    }

    @Test
    public void delete() throws Exception {

        assertTrue(printStorageService.delete(FILE));
    }

    @Test
    public void get() throws Exception {
        assertEquals(CONTENT, IOUtils.toString(printStorageService.get(FILE), Charset.defaultCharset()));
    }

}