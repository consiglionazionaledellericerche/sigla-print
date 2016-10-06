package it.cnr.si.config;

import it.cnr.si.service.ExcelService;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by francesco on 12/09/16.
 */

@Configuration
public class PrintConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintConfiguration.class);

    @Autowired
    private QueueConfiguration queueConfiguration;

    @Autowired
    private ExcelService excelService;
    
    @Value("#{'${print.queue.priorita}'.split(',')}")
    private List<String> queuePriorita;


    @Scheduled(fixedDelayString = "${print.scheduler}")
    public void printScheduler() {
        LOGGER.info("Start scheduler at {}", ZonedDateTime.now());
    	for (String priorita : queuePriorita) {
    		queueConfiguration.queuePrintApplication(priorita).add(priorita);
		}
        Optional.ofNullable(excelService.print()).map(map -> excelService.executeExcel(map));
    	
    }
}