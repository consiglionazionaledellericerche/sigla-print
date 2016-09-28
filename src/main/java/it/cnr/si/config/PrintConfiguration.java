package it.cnr.si.config;

import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.hazelcast.config.Config;
import com.hazelcast.config.ManagementCenterConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;

/**
 * Created by francesco on 12/09/16.
 */

@Configuration
public class PrintConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintConfiguration.class);
    @Autowired
    private QueueConfiguration queueConfiguration;
    
    @Value("#{'${queue.sigla.priorita}'.split(',')}")
    private List<String> queuePriorita;


    @Scheduled(fixedDelayString = "${scheduler.print}")
    public void printScheduler() {
        LOGGER.info("Start scheduler at {}", Calendar.getInstance());
    	for (String priorita : queuePriorita) {
    		queueConfiguration.queuePrintApplication(priorita);
		}
    }


    @Bean
    public Config config() {
        Config config = new Config();
        config.setInstanceName("sigla-print-server");
        ManagementCenterConfig managementCenterConfig = new ManagementCenterConfig();
        managementCenterConfig.setEnabled(true);
        managementCenterConfig.setUrl("http://localhost:8980");
        config.setManagementCenterConfig(managementCenterConfig);
        return config;
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        HazelcastCacheManager cacheManager = new HazelcastCacheManager(hazelcastInstance);
        return cacheManager;
    }    	
}
