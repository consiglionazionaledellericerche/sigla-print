package it.cnr.si.service;

import it.cnr.si.exception.JasperRuntimeException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

@Service
public class CacheService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
	public static final String JASPER_CACHE = "jasper-cache";
	@Value("${cnr.gitlab.url}")
	private String gitlabUrl;

	private RestTemplate restTemplate = new RestTemplate();

	@Value("${cnr.gitlab.token}")
	private String gitlabToken;
	
	@CacheEvict(cacheNames = JASPER_CACHE, key = "#key")
	public void evict(String key) {
		LOGGER.info("evicted {}", key);
	}

	@Cacheable(cacheNames = JASPER_CACHE, key = "#key")
	public byte[] imageReport(String key) {
		byte[] image = restTemplate.getForObject(gitlabUrl + key + "?private_token={private_token}",
				byte[].class, gitlabToken);
		LOGGER.debug(key);
		return image;
	}

	@Cacheable(cacheNames = JASPER_CACHE, key = "#key")
	public JasperReport jasperSubReport(String key) {
		String jrXml = restTemplate.getForObject(gitlabUrl + key + "?private_token={private_token}",
				String.class, gitlabToken);
		LOGGER.debug(jrXml);
		LOGGER.info("creating jasper report: {}", key);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
			return JasperCompileManager.compileReport(inputStream);
		} catch (JRException e) {
			throw new JasperRuntimeException("unable to compile report id " + key, e);
		}
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
