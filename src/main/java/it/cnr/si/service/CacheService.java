package it.cnr.si.service;

import it.cnr.si.config.GitLabConfiguration;
import it.cnr.si.exception.JasperRuntimeException;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.io.IOUtils;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CacheService {
	private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
	public static final String JASPER_CACHE = "jasper-cache";

	@Autowired
	private GitLabApi gitLabApi;

	@Autowired
	private GitLabConfiguration gitLabConfiguration;
	@Autowired
	private Project project;
	
	@CacheEvict(cacheNames = JASPER_CACHE, key = "#key")
	public void evict(String key) {
		LOGGER.info("evicted {}", key);
	}

	@Cacheable(cacheNames = JASPER_CACHE, key = "#key")
	public byte[] imageReport(String key) {
		try {
			final RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(project, key, gitLabConfiguration.getRef());
			LOGGER.debug(key);
			return file.getDecodedContentAsBytes();
		} catch (GitLabApiException e) {
			throw new RuntimeException(e);
		}
	}

	@Cacheable(cacheNames = JASPER_CACHE, key = "#key")
	public JasperReport jasperSubReport(String key) {
		try {
			final RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(project, key, gitLabConfiguration.getRef());
			String jrXml = file.getDecodedContentAsString();
			LOGGER.debug(jrXml);
			LOGGER.info("creating jasper report: {}", key);
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
				return JasperCompileManager.compileReport(inputStream);
			} catch (JRException e) {
				throw new JasperRuntimeException("unable to compile report id " + key, e);
			}
		} catch (GitLabApiException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Cacheable(cacheNames = JASPER_CACHE, key = "#key")
	public JasperReport jasperReport(String key) {
		try {
			final RepositoryFile file = gitLabApi.getRepositoryFileApi().getFile(project, key, gitLabConfiguration.getRef());
			String jrXml = file.getDecodedContentAsString();
			LOGGER.debug(jrXml);
			LOGGER.info("creating jasper report: {}", key);
			try {
				InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
				return JasperCompileManager.compileReport(inputStream);
			} catch (JRException e) {
				throw new JasperRuntimeException("unable to compile report id " + key, e);
			}
		} catch (GitLabApiException e) {
			throw new RuntimeException(e);
		}
	}
}
