package it.cnr.si.service;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.exception.JasperRuntimeException;
import it.cnr.si.repository.PrintRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * Created by francesco on 09/09/16.
 */

@Service
public class PrintService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintService.class);
    public static final String JASPER_CACHE = "jasper-cache";

    @Autowired
    private Connection connection;

    @Value("${cnr.gitlab.url}")
    private String gitlabUrl;


    @Value("${cnr.gitlab.token}")
    private String gitlabToken;

    @Autowired
    private PrintRepository printRepository;

    private RestTemplate restTemplate = new RestTemplate();

    private final CounterService counterService;

    @Autowired
    public PrintService(CounterService counterService) {
        this.counterService = counterService;
    }


	public ByteArrayOutputStream print(JasperReport jasperReport) {
		JasperPrint jasperPrint = getJasperPrint(jasperReport, null, connection);
		return print(jasperPrint);
	}

    public ByteArrayOutputStream print(JasperPrint print) {

        this.counterService.increment("services.system.PrintService.invoked");

        JRPdfExporter exporter = new JRPdfExporter();

        ExporterInput exporterInput = new SimpleExporterInput(print);
        exporter.setExporterInput(exporterInput);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        exporter.setConfiguration(configuration);

        try {
            exporter.exportReport();
        } catch (JRException e) {
            throw new JasperRuntimeException("unable to export report " + print.toString(), e);
        }

        return outputStream;
    }

    private JasperPrint getJasperPrint(JasperReport jasperReport, Map<String, Object> parameters, Connection connection)  {

        LOGGER.info("connection = {}", connection);
        try {
            return JasperFillManager.fillReport(jasperReport,
                        parameters, connection);
        } catch (JRException e) {
            throw new JasperRuntimeException("unable to process report", e);
        }
    }


    @CacheEvict(cacheNames = JASPER_CACHE, key = "#key")
    public void evict(String key) {
        LOGGER.info("evicted {}", key);
    }


    @Cacheable(cacheNames = JASPER_CACHE, key = "#key")
    public JasperReport jasperReport(String key) {

        String jrXml = restTemplate.getForObject(gitlabUrl + key + "?private_token={private_token}",
                String.class, gitlabToken);

        LOGGER.debug(jrXml);

        LOGGER.info("creating jasper report: {}", key);

        try {
            InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
            return JasperCompileManager.compileReport(inputStream);
        } catch (JRException e) {
            throw new JasperRuntimeException("unable to compile report id " + key, e);
        }
    }

    
	private Date getInitDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		LOGGER.info("InitDate " + cal.getTime());
		return cal.getTime();
	}

	private Date getFinalDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		LOGGER.info("FinalDate " + cal.getTime());
		return cal.getTime();
	}
	
	@Transactional(readOnly = true)
	public void print(Integer priorita) {
        Long pgStampa = printRepository.findReportToExecute(priorita, getInitDate(), getFinalDate());        
        PrintSpooler printSpooler = printRepository.findOneForUpdate(pgStampa);
        String key =  printSpooler.getReport().substring(0, printSpooler.getReport().indexOf(".jasper")).concat(".jrxml");
        JasperReport jasperReport = jasperReport(key);
        Map<String, Object> parameters = new HashMap<String, Object>();
        //TODO
        JasperPrint jasperPrint = getJasperPrint(jasperReport, parameters, connection);
        print(jasperPrint);
	}


}