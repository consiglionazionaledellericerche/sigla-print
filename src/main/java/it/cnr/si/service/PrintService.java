/*
 * Copyright (C) 2020  Consiglio Nazionale delle Ricerche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package it.cnr.si.service;

import io.micrometer.core.instrument.MeterRegistry;
import it.cnr.si.config.DatabaseConfiguration;
import it.cnr.si.domain.sigla.*;
import it.cnr.si.exception.JasperRuntimeException;
import it.cnr.si.repository.ParametriEnteRepository;
import it.cnr.si.repository.PrintParamRepository;
import it.cnr.si.repository.PrintRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.repo.InputStreamResource;
import net.sf.jasperreports.repo.ReportResource;
import net.sf.jasperreports.repo.RepositoryService;
import net.sf.jasperreports.repo.Resource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by francesco on 09/09/16.
 */

@Service
public class PrintService implements InitializingBean {

    public static final String TIMES_NEW_ROMAN = "Times New Roman";
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintService.class);
    private final MeterRegistry meterRegistry;
    @Autowired
    private DatabaseConfiguration databaseConfiguration;
    @Autowired
    private PrintRepository printRepository;
    @Autowired
    private PrintParamRepository printParamRepository;
    @Autowired
    private ParametriEnteRepository parametriEnteRepository;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private MailService mailService;
    @Value("${file.separator}")
    private String fileSeparator;
    @Value("${print.server.url}")
    private String serverURL;
    @Autowired
    private PrintStorageService storageService;
    @Value("${dir.image}")
    private String dirImage;
    @Value("${java.io.tmpdir}")
    private String tempDir;
    @Value("${print.max.page.size}")
    private Integer maxPageSize;
    @Value("${print.virtualizer.enable}")
    private Boolean virtualizerEnable;

    @Autowired
    public PrintService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public JRFileVirtualizer fileVirtualizer() {
        return new JRFileVirtualizer(maxPageSize, tempDir);
    }

    public ByteArrayOutputStream print(JasperPrint print, JRVirtualizer jrVirtualizer) {

        this.meterRegistry.get("services.system.PrintService.invoked");

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
        } finally {
            if (virtualizerEnable)
                jrVirtualizer.cleanup();
        }

        return outputStream;
    }

    public JasperPrint jasperPrint(JasperReport jasperReport, PrintSpooler printSpooler, JRVirtualizer jrVirtualizer) {
        LOGGER.info("jasperReportName = {}", printSpooler.getReport());
        Connection conn = null;
        try {
            HashMap<String, Object> parameters = printSpooler.getParameters();
            final Optional<String> reportDataSource = Optional.ofNullable(parameters)
                    .flatMap(stringObjectHashMap -> Optional.ofNullable(stringObjectHashMap.get(JRParameter.REPORT_DATA_SOURCE)))
                    .filter(String.class::isInstance)
                    .map(String.class::cast);

            DefaultJasperReportsContext defaultJasperReportsContext = DefaultJasperReportsContext.getInstance();
            if (reportDataSource.isPresent()) {
                parameters.put(JRParameter.REPORT_DATA_SOURCE, new JsonDataSource(new ByteArrayInputStream(reportDataSource.get().getBytes())));
            } else {
                conn = databaseConfiguration.connection();
            }

            parameters.put("DIR_IMAGE", dirImage);
            if (virtualizerEnable)
                parameters.put(JRParameter.REPORT_VIRTUALIZER, jrVirtualizer);

            defaultJasperReportsContext.setProperty("net.sf.jasperreports.awt.ignore.missing.font", "true");
            defaultJasperReportsContext.setProperty("net.sf.jasperreports.default.pdf.font.name", TIMES_NEW_ROMAN);
            defaultJasperReportsContext.setProperty("net.sf.jasperreports.default.font.name", TIMES_NEW_ROMAN);

            JasperReportsContext jasperReportsContext = new CacheAwareJasperReportsContext(defaultJasperReportsContext);
            JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
            if (reportDataSource.isPresent()) {
                return jasperFillManager.fill(jasperReport,
                        parameters);
            } else {
                return jasperFillManager.fill(jasperReport,
                        parameters,
                        conn);
            }
        } catch (JRRuntimeException | SQLException | JRException e) {
            throw new JasperRuntimeException("unable to process report", e);
        } finally {
            Optional.ofNullable(conn)
                    .ifPresent(connection -> {
                        try {
                            connection.commit();
                            connection.close();
                        } catch (SQLException e) {
                            throw new JasperRuntimeException("unable to process report", e);
                        }
                    });
        }
    }

    public Long print(Integer priorita) {
        return printRepository.findReportToExecute(priorita);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PrintSpooler print(Long pgStampa) {
        PrintSpooler printSpooler = printRepository.findById(pgStampa).orElseThrow(() -> new RuntimeException("Cannot find print with id:" + pgStampa));
        if (printSpooler.canExecute()) {
            printSpooler.setStato(PrintState.X);
            printSpooler.setDuva(Timestamp.from(ZonedDateTime.now().toInstant()));
            printRepository.save(printSpooler);
            return printSpooler;
        } else {
            LOGGER.warn("Cannot execute report {} width stato {} and data prossima esecuzione {}",
                    pgStampa, printSpooler.getStato(), printSpooler.getDtProssimaEsecuzione());
            return null;
        }

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void error(PrintSpooler printSpooler, Throwable _ex) {
        LOGGER.error("Error executing report with pgStampa: {}", printSpooler.getPgStampa(), _ex);
        printSpooler.setStato(PrintState.E);
        printSpooler.setDuva(Timestamp.from(ZonedDateTime.now().toInstant()));
        printSpooler.setErrore(Optional.ofNullable(_ex.getCause()).map(Throwable::getMessage).orElse(_ex.getMessage()));
        printRepository.save(printSpooler);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long executeReport(JasperPrint jasperPrint, JRVirtualizer jrVirtualizer, Long pgStampa, String name, String userName) {
        ByteArrayOutputStream byteArrayOutputStream = print(jasperPrint, jrVirtualizer);
        try {
            String collect = Arrays.asList(userName, name).stream().collect(Collectors.joining(fileSeparator));
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            storageService.write(collect, byteArray).thenAccept(aVoid -> {
                PrintSpooler printSpooler = printRepository.findById(pgStampa).orElseThrow(() -> new RuntimeException("Cannot find print with id:" + pgStampa));
                final String oldFileName = printSpooler.getNomeFile();
                if (printSpooler.getDtProssimaEsecuzione() != null) {
                    GregorianCalendar data_da = (GregorianCalendar) GregorianCalendar.getInstance();
                    data_da.setTime(printSpooler.getDtProssimaEsecuzione());
                    int addType = Calendar.DATE;
                    if (printSpooler.getTiIntervallo().equals(TipoIntervallo.G))
                        addType = Calendar.DATE;
                    else if (printSpooler.getTiIntervallo().equals(TipoIntervallo.S))
                        addType = Calendar.WEEK_OF_YEAR;
                    else if (printSpooler.getTiIntervallo().equals(TipoIntervallo.M))
                        addType = Calendar.MONTH;
                    data_da.add(addType, printSpooler.getIntervallo());
                    printSpooler.setDtProssimaEsecuzione(new Timestamp(data_da.getTimeInMillis()));
                }
                printSpooler.setStato(PrintState.S);
                printSpooler.setServer(serverURL.concat("/api/v1/get/print"));
                printSpooler.setDuva(Timestamp.from(ZonedDateTime.now().toInstant()));
                printSpooler.setNomeFile(name);
                printRepository.save(printSpooler);
                if (printSpooler.getFlEmail()) {
                    File output = null;
                    try {
                        output = File.createTempFile(collect, null);
                        final InputStream inputStream = storageService.get(collect);
                        try (FileOutputStream out = new FileOutputStream(output)) {
                            IOUtils.copy(inputStream, out);
                            inputStream.close();
                        }
                        Optional.ofNullable(oldFileName)
                                .map(nomeFile -> Arrays.asList(userName, nomeFile).stream().collect(Collectors.joining(fileSeparator)))
                                .ifPresent(id -> storageService.delete(id));
                        StringBuffer bodyText = new StringBuffer(printSpooler.getEmailBody() == null ? "" : printSpooler.getEmailBody());
                        bodyText.append("<html><body bgcolor=\"#ffffff\" text=\"#000000\"><BR><BR><b>Nota di riservatezza:</b><br>");
                        bodyText.append("La presente comunicazione ed i suoi allegati sono di competenza solamente del sopraindicato destinatario. ");
                        bodyText.append("Qualsiasi suo utilizzo, comunicazione o diffusione non autorizzata e' proibita.<br>");
                        bodyText.append("Qualora riceviate detta e-mail per errore, vogliate distruggerla.<br><br>");
                        bodyText.append("<b>Attenzione: </b><br>");
                        bodyText.append("Questa e' una e-mail generata automaticamente da un server non presidiato, La preghiamo di non rispondere. ");
                        bodyText.append("Questa casella di posta elettronica non e' abilitata alla ricezione di messaggi.<br>");
                        if (printSpooler.getDtProssimaEsecuzione() != null) {
                            StringTokenizer indirizzi = new StringTokenizer(printSpooler.getEmailA(), ",");
                            bodyText.append("Se non desidera piu' far parte della lista di distribuzione della stampa allegata clicchi ");
                            while (indirizzi.hasMoreElements()) {
                                String indirizzo = (String) indirizzi.nextElement();
                                String messaggio = "<a href=\"https://contab.cnr.it/SIGLA/cancellaSchedulazione.do?pgStampa=pg" +
                                        String.valueOf(printSpooler.getPgStampa()).trim() + "&indirizzoEMail=" + indirizzo + "\">qui</a> per cancellarsi.<br></body></html>";
                                mailService.send(printSpooler.getEmailSubject(), bodyText.toString().concat(messaggio), indirizzo,
                                        printSpooler.getEmailCc(), printSpooler.getEmailCcn(), output, name);
                            }
                        } else {
                            bodyText.append("</body></html>");
                            mailService.send(printSpooler.getEmailSubject(), bodyText.toString(), printSpooler.getEmailA(),
                                    printSpooler.getEmailCc(), printSpooler.getEmailCcn(), output, name);
                        }
                    } catch (Exception ex) {
                        LOGGER.error("Error while sending email for report pgStampa: {}", pgStampa, ex);
                    } finally {
                        Optional.ofNullable(output)
                                .ifPresent(File::delete);
                    }
                }
            });
        } catch (Exception e) {
            error(printRepository.findById(pgStampa).orElseThrow(() -> new RuntimeException("Cannot find print with id:" + pgStampa)), e);
        }
        return pgStampa;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteReport(Long pgStampa) {
        LOGGER.info("Try to delete report pgStampa: {}", pgStampa);
        PrintSpooler printSpooler = printRepository.findById(pgStampa).orElseThrow(() -> new RuntimeException("Cannot find print with id:" + pgStampa));
        String path = Arrays.asList(printSpooler.getUtcr(), printSpooler.getName()).stream().collect(Collectors.joining(fileSeparator));
        storageService.delete(path);
        printRepository.delete(printSpooler);
    }

    public void deleteReport() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(Optional.ofNullable(parametriEnteRepository.findCancellaStampe())
                .orElse(BigDecimal.valueOf(30)).longValue());
        Iterable<Long> findReporsToDelete = printRepository.findReportsToDelete(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
        for (Long pgStampa : findReporsToDelete) {
            deleteReport(pgStampa);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serverURL = Optional.ofNullable(serverURL)
                .filter(s -> s.equalsIgnoreCase("http://localhost:8080"))
                .map(s -> {
                    try {
                        final String hostAddress = InetAddress.getLocalHost().getHostAddress();
                        return "http://".concat(hostAddress).concat(":8080");
                    } catch (UnknownHostException e) {
                        return s;
                    }
                })
                .orElse(serverURL);
        LOGGER.info("Server URL: {}", serverURL);
    }


    class CacheAwareJasperReportsContext implements JasperReportsContext {

        private JasperReportsContext jasperReportsContext;

        public CacheAwareJasperReportsContext(JasperReportsContext jasperReportsContext) {
            this.jasperReportsContext = jasperReportsContext;
        }

        @Override
        public Object getValue(String key) {
            return jasperReportsContext.getValue(key);
        }

        @Override
        public Object getOwnValue(String key) {
            return jasperReportsContext.getOwnValue(key);
        }

        @Override
        public void setValue(String key, Object value) {
            jasperReportsContext.setValue(key, value);
        }

        @Override
        public <T> List<T> getExtensions(Class<T> extensionType) {
            if (extensionType.isAssignableFrom(RepositoryService.class)) {
                return (List<T>) Arrays.asList(new CacheAwareRepositoryService());
            } else {
                return jasperReportsContext.getExtensions(extensionType).stream().distinct().collect(Collectors.toList());
            }
        }

        @Override
        public String getProperty(String key) {
            return jasperReportsContext.getProperty(key);
        }

        @Override
        public void setProperty(String key, String value) {
            jasperReportsContext.setProperty(key, value);

        }

        @Override
        public void removeProperty(String key) {
            jasperReportsContext.removeProperty(key);
        }

        @Override
        public Map<String, String> getProperties() {
            return jasperReportsContext.getProperties();
        }
    }


    class CacheAwareRepositoryService implements RepositoryService {

        @Override
        public Resource getResource(String uri) {
            throw new NotImplementedException("unable to get resource " + uri);
        }

        @Override
        public void saveResource(String uri, Resource resource) {
            throw new NotImplementedException("cannot save resource " + uri + " " + resource.getName());
        }

        @Override
        public <K extends Resource> K getResource(String uri, Class<K> resourceType) {

            if (resourceType.isAssignableFrom(ReportResource.class)) {
                String key = uri.substring(0, uri.indexOf(".jasper")).concat(".jrxml");
                ReportResource reportResource = new ReportResource();
                JasperReport report = cacheService.jasperSubReport(key);
                reportResource.setReport(report);
                return (K) reportResource;
            } else if (resourceType.isAssignableFrom(InputStreamResource.class)) {
                InputStreamResource inputStreamResource = new InputStreamResource();
                byte[] bytes = cacheService.imageReport(uri);
                InputStream inputStream = new ByteArrayInputStream(bytes);
                inputStreamResource.setInputStream(inputStream);
                return (K) inputStreamResource;
            }

            throw new NotImplementedException("unable to serve resource " + uri + " of type " + resourceType.getCanonicalName());
        }

    }
    @Transactional(propagation = Propagation.REQUIRED)
    public Long createPrintSpooler(PrintSpooler printSpooler) {
        LOGGER.info("Create Record printSpooler: {}", printSpooler);
        if (!Optional.ofNullable(printSpooler)
                .flatMap(printSpooler1 -> Optional.ofNullable(printSpooler.getPgStampa()))
                .isPresent()) {
            printSpooler.setPgStampa(
                    Optional.ofNullable(printRepository.findMaxPgStampa())
                        .orElse(BigDecimal.ONE.longValue())
            );
        }
        printRepository.save( printSpooler);
        return printSpooler.getPgStampa();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PrintSpooler findPrintSpoolerById(Long pg_stampa) {
        LOGGER.info("get Record printSpoller: {}", pg_stampa);
        return printRepository.findById(pg_stampa).orElseThrow(() -> new RuntimeException("Cannot find print with id:" + pg_stampa));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PrintSpooler updatePrintSpooler(Long pg_stampa,PrintSpooler printSpoller) {
        LOGGER.info("update Record printSpoller: {}", pg_stampa);
        PrintSpooler printSpoolerDB= printRepository.findById(pg_stampa).orElseThrow(() -> new RuntimeException("Cannot find print with id:" + pg_stampa));
        printSpoller.setPgStampa( printSpoolerDB.getPgStampa());
        return printRepository.save( printSpoller);
    }

}