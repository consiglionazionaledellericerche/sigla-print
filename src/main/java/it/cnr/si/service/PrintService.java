package it.cnr.si.service;

import it.cnr.si.exception.JasperRuntimeException;
import net.sf.jasperreports.engine.*;
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
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

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



    private RestTemplate restTemplate = new RestTemplate();

    private final CounterService counterService;

    @Autowired
    public PrintService(CounterService counterService) {
        this.counterService = counterService;
    }


    public ByteArrayOutputStream print(JasperReport jasperReport) {

        this.counterService.increment("services.system.PrintService.invoked");

        JasperPrint print = getJasperPrint(jasperReport, new HashMap<>(), connection);

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
            throw new JasperRuntimeException("unable to export report " + jasperReport.toString(), e);
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


}
