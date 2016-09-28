package it.cnr.si.config;

import it.cnr.si.service.PrintService;

import java.util.Calendar;
import java.util.List;

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
    private PrintService printService;
    
    @Autowired
    private QueueService queueService;
    
    @Value("#{'${queue.sigla.priorita}'.split(',')}")
    private List<String> queuePriorita;

    public PrintConfiguration(PrintService printService) {
        this.printService = printService;
    }


    @Scheduled(fixedDelayString = "${scheduler.print}")
    public void printScheduler() {
        LOGGER.info("Start scheduler at {}", Calendar.getInstance());
    	for (String priorita : queuePriorita) {
    		queueService.queuePrintApplication(priorita);
		}
    }

}
