package it.cnr.si.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;

/**
 * Created by francesco on 09/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintServiceTest {

    @Autowired
    private PrintService printService;

    @Test
    public void print() throws Exception {
        ByteArrayOutputStream baos = printService.print(1234l);
        assertEquals(919, baos.size());
    }

}