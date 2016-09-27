package it.cnr.si.config;

import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import it.cnr.si.service.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.stream.Stream;

/**
 * Created by francesco on 12/09/16.
 */

@Configuration
public class PrintConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintConfiguration.class);

    private PrintService printService;

    public PrintConfiguration(PrintService printService) {
        this.printService = printService;
    }


    @Scheduled(fixedDelayString = "${scheduler.print}")
    public void printScheduler() {

        LOGGER.warn("recuperare id stampa da coda");

        Stream
                .of(1L,100L,200L,12345L)
                .peek(id -> LOGGER.info("print {}", id))
                .forEach(printService::print);

    }


    @Bean
    public Config config() {
        Config config = new Config();
        config.setInstanceName("sigla-print-server");
        return config;
    }

          @Bean
      public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
              HazelcastCacheManager cacheManager = new HazelcastCacheManager(hazelcastInstance);
          return cacheManager;
      }




}
