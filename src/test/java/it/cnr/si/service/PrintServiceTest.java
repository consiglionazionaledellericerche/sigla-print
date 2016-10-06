package it.cnr.si.service;

import static org.junit.Assert.assertEquals;
import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintSpoolerParam;
import it.cnr.si.domain.sigla.PrintSpoolerParamKey;
import it.cnr.si.repository.PrintRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
    private CacheService cacheService;

    @Autowired
    private PrintRepository printRepository;

    @Value("${file.separator}")
	private String fileSeparator;

	@Value("${print.output.dir}")
	private String printOutputDir;
    
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

    	ByteArrayOutputStream baos = printService.print(
    			printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler.getParameters()));
        assertEquals(8711, baos.size());
    }
	
    @Test
    public void testCache() {
    	cacheService.jasperReport(IDREPORT);
    	cacheService.jasperReport(IDREPORT);
        
    	cacheService.jasperSubReport(IDSUBREPORT);
    	cacheService.jasperSubReport(IDSUBREPORT);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(4133, bais.available());
        bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(4133, bais.available());
        
        cacheService.evict(IDREPORT);
        
        cacheService.jasperReport(IDREPORT);
        cacheService.jasperReport(IDREPORT);

        cacheService.evict(IDSUBREPORT);        
        cacheService.jasperSubReport(IDSUBREPORT);
        cacheService.jasperSubReport(IDSUBREPORT);

        cacheService.evict(IDIMAGE);        
        bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(4133, bais.available());
        bais = new ByteArrayInputStream(cacheService.imageReport(IDIMAGE));
        assertEquals(4133, bais.available());
    }

}