package it.cnr.si.service;

import static org.junit.Assert.assertEquals;
import it.cnr.si.repository.PrintRepository;

import java.io.ByteArrayOutputStream;

import net.sf.jasperreports.engine.JasperReport;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by francesco on 09/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintServiceTest {

    public static final String ID = "reports/logs/batchlog.jrxml";
    @Autowired
    private PrintService printService;

    @Autowired
    private PrintRepository printRepository;

    @Test
    public void print() throws Exception {

        JasperReport jasperReport = printService.jasperReport("foo-123");
        ByteArrayOutputStream baos = printService.print(jasperReport);
        assertEquals(919, baos.size());
    }


    @Test
    public void testCache() {

        printService.jasperReport(ID);
        printService.jasperReport(ID);
        printService.jasperReport(ID);
        printService.evict(ID);
        printService.jasperReport(ID);
        printService.jasperReport(ID);
        printService.jasperReport(ID);

    }

}