package it.cnr.si.service;

import static org.junit.Assert.assertEquals;
import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintSpoolerParam;
import it.cnr.si.domain.sigla.PrintSpoolerParamKey;
import it.cnr.si.repository.PrintRepository;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
    	PrintSpooler printSpooler = new PrintSpooler((long)5760923);
    	printSpooler.setReport("/doccont/doccont/vpg_man_rev_ass.jasper");
    	Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aCd_cds", printSpooler), "075", String.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aCd_terzo", printSpooler), "%", String.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aDt_a", printSpooler), "2016/02/05", Date.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aDt_da", printSpooler), "2016/02/05", Date.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aEs", printSpooler), "2016", Integer.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aPg_a", printSpooler), "6246", Long.class.getCanonicalName()));
    	params.add(new PrintSpoolerParam(new PrintSpoolerParamKey("aPg_da", printSpooler), "6246", Long.class.getCanonicalName()));

    	printSpooler.setParams(params);

    	ByteArrayOutputStream baos = printService.executeReport(printSpooler);
        assertEquals(919, baos.size());
    }


    @Test
    public void testCache() {
        printService.jasperReport(ID, null);
        printService.jasperReport(ID, null);
        printService.jasperReport(ID, null);
        printService.evict(ID);
        printService.jasperReport(ID, null);
        printService.jasperReport(ID, null);
        printService.jasperReport(ID, null);

    }

}