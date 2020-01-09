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

import it.cnr.si.config.JasperSource;
import it.cnr.si.exception.JasperRuntimeException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

@Service
public class CacheService {
    public static final String JASPER_CACHE = "jasper-cache";
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
    @Autowired
    private JasperSource jasperSource;

    @CacheEvict(cacheNames = JASPER_CACHE, key = "#key")
    public void evict(String key) {
        LOGGER.info("evicted {}", key);
    }

    @Cacheable(cacheNames = JASPER_CACHE, key = "#key")
    public byte[] imageReport(String key) {
        try {
            LOGGER.debug(key);
            return jasperSource.getContentAsBytes(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(cacheNames = JASPER_CACHE, key = "#key")
    public JasperReport jasperSubReport(String key) {
        try {
            String jrXml = jasperSource.getContentAsString(key);
            LOGGER.debug(jrXml);
            LOGGER.info("creating jasper report: {}", key);
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
                return JasperCompileManager.compileReport(inputStream);
            } catch (JRException e) {
                throw new JasperRuntimeException("unable to compile report id " + key, e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Cacheable(cacheNames = JASPER_CACHE, key = "#key")
    public JasperReport jasperReport(String key) {
        try {
            String jrXml = jasperSource.getContentAsString(key);
            LOGGER.debug(jrXml);
            LOGGER.info("creating jasper report: {}", key);
            try {
                InputStream inputStream = IOUtils.toInputStream(jrXml, Charset.defaultCharset());
                return JasperCompileManager.compileReport(inputStream);
            } catch (JRException e) {
                throw new JasperRuntimeException("unable to compile report id " + key, e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
