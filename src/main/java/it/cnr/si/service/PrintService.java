package it.cnr.si.service;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintState;
import it.cnr.si.exception.JasperRuntimeException;
import it.cnr.si.repository.PrintRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
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
    public static final String JASPER_CACHE = "jasper-cache", IMAGE_CACHE = "image-cache";
	public static final java.text.DateFormat DATE_FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd");
	public static final java.text.DateFormat TIMESTAMP_FORMAT = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final DateFormat PDF_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

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
        	LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        	ctx.setClassLoader(getClass().getClassLoader());
        	ctx.setFileResolver(new FileResolver() {
        	        @Override
        	        public File resolveFile(String s) {
        	        	if (s.endsWith(".jasper")) {
							try {
	        	        		String key = s.substring(0, s.indexOf(".jasper")).concat(".jrxml");
	        	        		File subReportFile = File.createTempFile("SUBREPORT", ".jasper");
	        	        		FileOutputStream fileOutputStream = new FileOutputStream(subReportFile);
	        	        		jasperReport(key, fileOutputStream);
	        	        		return subReportFile;
							} catch (IOException e) {
								LOGGER.error("Cannot compile sub report", e);
							}
        	        	}
						try {
							File image = File.createTempFile("IMAGE", ".jpg");
	        	        	FileUtils.copyInputStreamToFile(imageReport(s), image);
	        	            return image;
						} catch (IOException e) {
							LOGGER.error("Cannot find image", e);
						}
						return null;
        	        }
        	    });        	
            return JasperFillManager.getInstance(ctx).fill(jasperReport,
                        parameters, connection);
        } catch (JRException e) {
            throw new JasperRuntimeException("unable to process report", e);
        }
    }


    @CacheEvict(cacheNames = JASPER_CACHE, key = "#key")
    public void evictReport(String key) {
        LOGGER.info("evicted {}", key);
    }

    @CacheEvict(cacheNames = IMAGE_CACHE, key = "#key")
    public void evictImage(String key) {
        LOGGER.info("evicted {}", key);
    }

    @Cacheable(cacheNames = IMAGE_CACHE, key = "#key")
    public InputStream imageReport(String key) {
    	byte[] image = restTemplate.getForObject(gitlabUrl + key + "?private_token={private_token}",
        		byte[].class, gitlabToken);
        LOGGER.debug(key);
        return new ByteArrayInputStream(image);
    }

    @Cacheable(cacheNames = JASPER_CACHE, key = "#key")
    public JasperReport jasperReport(String key, OutputStream outputStream) {

        String jrXml = restTemplate.getForObject(gitlabUrl + key + "?private_token={private_token}",
                String.class, gitlabToken);

        LOGGER.debug(jrXml);

        LOGGER.info("creating jasper report: {}", key);

        try {
            InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
            if (outputStream != null) {
            	JasperCompileManager.compileReportToStream(inputStream, outputStream);
            	return null;
            }
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
	
	@Transactional(readOnly = false)
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Transactional(readOnly = false)
	public PrintSpooler executeReport(PrintSpooler printSpooler) {
		int indexEx = printSpooler.getReport().indexOf(".jasper"),
				indexLast = printSpooler.getReport().lastIndexOf("/");		
		String key =  printSpooler.getReport().substring(0, indexEx).concat(".jrxml"),
				path = printSpooler.getReport().substring(0, indexLast + 1),
				name = printSpooler.getReport().substring(indexLast + 1, indexEx);
		JasperReport jasperReport = jasperReport(key, null);			
		
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        for (it.cnr.si.domain.sigla.PrintSpoolerParam printSpoolerParam : printSpooler.getParams()) {
        	Serializable valoreParametro = null;
        	try{
        		Class classe = Class.forName(printSpoolerParam.getParamType());
        		if (classe.equals(java.util.Date.class)){
        			valoreParametro = DATE_FORMAT.parse(printSpoolerParam.getValoreParam());
        		}else if (classe.equals(java.util.Date.class)){
        			valoreParametro = TIMESTAMP_FORMAT.parse(printSpoolerParam.getValoreParam());
        		}else{
        			Constructor costr =  classe.getConstructor(String.class);
        			valoreParametro = (Serializable) costr.newInstance(printSpoolerParam.getValoreParam());
        		}
        	}catch(ClassCastException _ex){
        		valoreParametro = printSpoolerParam.getValoreParam();
    		}catch(Exception _ex){
    			LOGGER.error("Error in parameter conversion", _ex);
    		}
	        parameters.put(printSpoolerParam.getKey().getNomeParam(), valoreParametro);
		}
        parameters.put(JRParameter.REPORT_CONNECTION, connection);
        parameters.put("DIR_IMAGE", "/img/");        
        parameters.put("DIR_SUBREPORT", path);        
        JasperPrint jasperPrint = getJasperPrint(jasperReport, parameters, connection);
        ByteArrayOutputStream byteArrayOutputStream = print(jasperPrint);		
        try {
			FileUtils.writeByteArrayToFile(new File("/home/mspasiano/" + name + ".pdf"), byteArrayOutputStream.toByteArray());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return printSpooler;
	}
}