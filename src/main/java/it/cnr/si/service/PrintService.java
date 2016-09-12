package it.cnr.si.service;

import it.cnr.si.exception.JasperRuntimeException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by francesco on 09/09/16.
 */

@Service
public class PrintService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintService.class);

    @Value("classpath:/FirstJasper.jrxml")
    private Resource report;

    @Autowired
    private Connection connection;

    private final CounterService counterService;

    @Autowired
    public PrintService(CounterService counterService) {
        this.counterService = counterService;
    }


    public ByteArrayOutputStream print(long id) {

        this.counterService.increment("services.system.PrintService.invoked");

        LOGGER.info(report.getFilename());

        JasperReport jasperReport = jasperReport(id);

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
            throw new JasperRuntimeException("unable to export report " + id, e);
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

    private JasperReport jasperReport(long id) {
        try {
            return JasperCompileManager.compileReport(report.getInputStream());
        } catch (IOException | JRException e) {
            throw new JasperRuntimeException("unable to compile report id " + id, e);
        }
    }

}
