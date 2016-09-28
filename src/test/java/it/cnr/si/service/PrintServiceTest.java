package it.cnr.si.service;

import it.cnr.si.domain.Foo;
import it.cnr.si.repository.PrintRepository;
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

    @Autowired
    private PrintRepository fooRepository;

    @Test
    public void print() throws Exception {

        fooRepository.save(new Foo("titolone"));
        ByteArrayOutputStream baos = printService.print(1234l);
        assertEquals(919, baos.size());
    }

}