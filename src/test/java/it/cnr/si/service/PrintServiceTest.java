package it.cnr.si.service;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintSpoolerParam;
import it.cnr.si.domain.sigla.PrintSpoolerParamKey;
import it.cnr.si.repository.PrintRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;

import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by francesco on 09/09/16.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class PrintServiceTest {

    public static final String IDREPORT = "/logs/batchlog.jrxml", IDSUBREPORT = "/docamm/docamm/vpg_missione_subreport0.jrxml", IDIMAGE = "/img/CNR.JPG";
    @Autowired
    private PrintService printService;
    @Autowired
    private ExcelService excelService;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private PrintRepository printRepository;

    @Value("${file.separator}")
	private String fileSeparator;

	@Value("${print.output.dir}")
	private String printOutputDir;
    public static final int EXPECTED = 40_225;

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
        final JRFileVirtualizer jrFileVirtualizer = printService.fileVirtualizer();
    	ByteArrayOutputStream baos = printService.print(
    			printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler, jrFileVirtualizer), jrFileVirtualizer);
        assertTrue(baos.size() > 100_000);

    }
	
    @Test
    public void executeTwice() {
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
        final JRFileVirtualizer jrFileVirtualizer = printService.fileVirtualizer();

    	JasperPrint print1 = printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler, jrFileVirtualizer);
        ByteArrayOutputStream baos1 = printService.print(print1, jrFileVirtualizer);

        JasperPrint print2 = printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler, jrFileVirtualizer);
        ByteArrayOutputStream baos2 = printService.print(print2, jrFileVirtualizer);

        assertEquals(baos1.size(), baos2.size());    	
    }

    @Test
    public void testMissioniRimborso() throws JRException, IOException {
        PrintSpooler printSpooler = new PrintSpooler((long)5760923);
        printSpooler.setReport("/missioni/RimborsoMissione.jrxml");
        Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();
        params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
                JRParameter.REPORT_DATA_SOURCE, printSpooler),
                IOUtils.toString(this.getClass().getResourceAsStream("/missioni/rimborso.json"), StandardCharsets.UTF_8.name()),
                String.class.getCanonicalName()));
        printSpooler.setParams(params);
        final JRFileVirtualizer jrFileVirtualizer = printService.fileVirtualizer();
        JasperPrint print1 = printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler, jrFileVirtualizer);
        ByteArrayOutputStream baos = printService.print(print1, jrFileVirtualizer);
        assertTrue(baos.size() > 100_000);
    }

    @Test
    public void testMissioneOrdine() throws JRException, IOException {
        PrintSpooler printSpooler = new PrintSpooler((long)5760923);
        printSpooler.setReport("/missioni/OrdineMissione.jrxml");
        Set<PrintSpoolerParam> params = new HashSet<PrintSpoolerParam>();
        params.add(new PrintSpoolerParam(new PrintSpoolerParamKey(
                JRParameter.REPORT_DATA_SOURCE, printSpooler),
                IOUtils.toString(this.getClass().getResourceAsStream("/missioni/ordine.json"), StandardCharsets.UTF_8.name()),
                String.class.getCanonicalName()));
        printSpooler.setParams(params);
        final JRFileVirtualizer jrFileVirtualizer = printService.fileVirtualizer();
        JasperPrint print1 = printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler, jrFileVirtualizer);
        ByteArrayOutputStream baos = printService.print(print1, jrFileVirtualizer);
        assertTrue(baos.size() > 100_000);
    }
    @Test
    public void deleteReport() {
		printService.deleteReport();
		excelService.deleteXls();
    }
    
    @Test
    public void testCache() {
    	cacheService.jasperReport(IDREPORT);
    	cacheService.jasperReport(IDREPORT);
        
    	cacheService.jasperSubReport(IDSUBREPORT);
    	cacheService.jasperSubReport(IDSUBREPORT);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(EXPECTED, bais.available());
        bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(EXPECTED, bais.available());
        
        cacheService.evict(IDREPORT);
        
        cacheService.jasperReport(IDREPORT);
        cacheService.jasperReport(IDREPORT);

        cacheService.evict(IDSUBREPORT);        
        cacheService.jasperSubReport(IDSUBREPORT);
        cacheService.jasperSubReport(IDSUBREPORT);

        cacheService.evict(IDIMAGE);        
        bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(EXPECTED, bais.available());
        bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(EXPECTED, bais.available());
    }

}