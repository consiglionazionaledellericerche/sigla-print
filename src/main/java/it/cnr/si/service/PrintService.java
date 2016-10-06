package it.cnr.si.service;

import it.cnr.si.config.DatabaseConfiguration;
import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintState;
import it.cnr.si.domain.sigla.TipoIntervallo;
import it.cnr.si.exception.JasperRuntimeException;
import it.cnr.si.repository.PrintRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.FileResolver;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by francesco on 09/09/16.
 */

@Service
public class PrintService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrintService.class);

	@Autowired
	private DatabaseConfiguration databaseConfiguration;

	@Autowired
	private PrintRepository printRepository;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private MailService mailService;

	@Value("${file.separator}")
	private String fileSeparator;

	@Value("${print.output.dir}")
	private String printOutputDir;

	@Value("${print.server.url}")
	private String serverURL;
	
	private final CounterService counterService;

	@Autowired
	public PrintService(CounterService counterService) {
		this.counterService = counterService;
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
	
	@Transactional(propagation=Propagation.REQUIRES_NEW, readOnly=true)
	public JasperPrint jasperPrint(JasperReport jasperReport, Map<String, Object> parameters)  {
		LOGGER.info("jasperReport = {}", jasperReport);
		Connection conn = null;
		try {
			conn = databaseConfiguration.connection();
			LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
			ctx.setClassLoader(getClass().getClassLoader());
			ctx.setFileResolver(new FileResolver() {
				@Override
				public File resolveFile(String s) {
					if (s.endsWith(".jasper")) {
						String key = s.substring(0, s.indexOf(".jasper")).concat(".jrxml");
						return new File(cacheService.jasperSubReport(key));
					}
					try {
						File image = File.createTempFile("IMAGE", ".jpg");
						FileUtils.copyInputStreamToFile(new ByteArrayInputStream(cacheService.imageReport(s)), image);
						return image;
					} catch (IOException e) {
						LOGGER.error("Cannot find image", e);
					}
					return null;
				}
			});        	
			return JasperFillManager.getInstance(ctx).fill(jasperReport,
					parameters, conn);
		} catch (JRException | SQLException e) {
			throw new JasperRuntimeException("unable to process report", e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new JasperRuntimeException("unable to process report", e);
			}
		}
	}

	private Date getInitDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		LOGGER.debug("InitDate " + cal.getTime());
		return cal.getTime();
	}

	private Date getFinalDate(){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		LOGGER.debug("FinalDate " + cal.getTime());
		return cal.getTime();
	}
	
	@Transactional(propagation=Propagation.REQUIRES_NEW)
	public PrintSpooler print(Integer priorita) {
		Long pgStampa = printRepository.findReportToExecute(priorita, getInitDate(), getFinalDate());    
		if (pgStampa != null) {
			PrintSpooler printSpooler = printRepository.findOneForUpdate(pgStampa);
			printSpooler.setStato(PrintState.X);
			printRepository.save(printSpooler);
			return printSpooler;
		}
		return null;
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)	
	public Long executeReport(JasperPrint jasperPrint, Long pgStampa, String name, String userName) {
		ByteArrayOutputStream byteArrayOutputStream = print(jasperPrint);
		try {
			File output = new File(Arrays.asList(printOutputDir,userName, name).stream().collect(Collectors.joining(fileSeparator)));
			FileUtils.writeByteArrayToFile(output, byteArrayOutputStream.toByteArray());
			PrintSpooler printSpooler = printRepository.findOneForUpdate(pgStampa);
	        if (printSpooler.getDtProssimaEsecuzione() != null){
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
			printSpooler.setNomeFile(name);
			printRepository.save(printSpooler);
            if (printSpooler.getFlEmail()){
            	try {
                	StringBuffer bodyText = new StringBuffer(printSpooler.getEmailBody()==null?"":printSpooler.getEmailBody());
                	bodyText.append("<html><body bgcolor=\"#ffffff\" text=\"#000000\"><BR><BR><b>Nota di riservatezza:</b><br>");
                	bodyText.append("La presente comunicazione ed i suoi allegati sono di competenza solamente del sopraindicato destinatario. ");
                	bodyText.append("Qualsiasi suo utilizzo, comunicazione o diffusione non autorizzata e' proibita.<br>");
                	bodyText.append("Qualora riceviate detta e-mail per errore, vogliate distruggerla.<br><br>");
                	bodyText.append("<b>Attenzione: </b><br>");
                	bodyText.append("Questa e' una e-mail generata automaticamente da un server non presidiato, La preghiamo di non rispondere. ");
                	bodyText.append("Questa casella di posta elettronica non e' abilitata alla ricezione di messaggi.<br>");
                	if (printSpooler.getDtProssimaEsecuzione() != null){
                		StringTokenizer indirizzi = new StringTokenizer(printSpooler.getEmailA(),",");
            			bodyText.append("Se non desidera piu' far parte della lista di distribuzione della stampa allegata clicchi ");
                		while(indirizzi.hasMoreElements()){
                			String indirizzo = (String)indirizzi.nextElement();
                			String messaggio = "<a href=\"https://contab.cnr.it/SIGLA/cancellaSchedulazione.do?pgStampa=pg"+
                					String.valueOf(printSpooler.getPgStampa()).trim()+"&indirizzoEMail="+indirizzo+"\">qui</a> per cancellarsi.<br></body></html>";
                			mailService.send(printSpooler.getEmailSubject(), bodyText.toString().concat(messaggio), indirizzo, 
                					printSpooler.getEmailCc(), printSpooler.getEmailCcn(), output, name);
                		}
                	}else{
                    	bodyText.append("</body></html>");
            			mailService.send(printSpooler.getEmailSubject(), bodyText.toString(), printSpooler.getEmailA(), 
            					printSpooler.getEmailCc(), printSpooler.getEmailCcn(), output, name);
                	}            		
            	} catch (Exception ex) {
            		LOGGER.error("Error while sending email for report pgStampa: {}", pgStampa, ex);
            	}
            }			
		} catch (IOException e) {
			LOGGER.error("Error executing report pgStampa: {}", pgStampa, e);
			PrintSpooler printSpooler = printRepository.findOneForUpdate(pgStampa);
			printSpooler.setStato(PrintState.E);
			printSpooler.setErrore(e.getMessage());
			printRepository.save(printSpooler);			
		}
		return pgStampa;
	}
}