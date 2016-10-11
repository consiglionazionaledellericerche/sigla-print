package it.cnr.si.config;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.service.CacheService;
import it.cnr.si.service.PrintService;

import java.util.List;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

@Configuration
public class QueueConfiguration implements InitializingBean{
    private static final String SIGLA_PRIORITA = "SIGLA_PRIORITA_";
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConfiguration.class);    

    @Value("#{'${print.queue.priorita}'.split(',')}")
    private List<String> queuePriorita;
    
    @Autowired
    private HazelcastInstance hazelcastInstance;
    
    @Autowired
    private PrintService printService;

    @Autowired
    private CacheService cacheService;
    
    public IQueue<String> queuePrintApplication(String priorita) {
        return hazelcastInstance.getQueue(SIGLA_PRIORITA.concat(priorita));
    }
    
	@Override
	public void afterPropertiesSet() throws Exception {
		ItemListener<String> printApplicationListener = new ItemListener<String>() {
            @Override
            public void itemAdded(ItemEvent<String> itemEvent) {
            	String priorita = itemEvent.getItem();
                LOGGER.debug("PrintApplicationListener {} {}", priorita, itemEvent.getEventType().getType());
                boolean removed = queuePrintApplication(priorita).remove(priorita);
                LOGGER.trace("PrintApplicationListener {} {}", priorita, removed ? "removed" : "not removed");
                if (removed) {
                    LOGGER.trace("PrintApplicationListener consuming {}", priorita);
                    PrintSpooler print = printService.print(Integer.valueOf(priorita));
                    if (print != null) {
                    	try {
                        	JasperPrint jasperPrint = printService.jasperPrint(cacheService.jasperReport(print.getKey()), print);
                        	printService.executeReport(jasperPrint, print.getPgStampa(), 
                        			print.getName(), 
                        			print.getUtcr());                    		
                    	} catch (Exception _ex) {
                    		printService.error(print, _ex);
                    	}
                    }
                    LOGGER.trace("PrintApplicationListener consumed {}", priorita);
                }
            }
            @Override
            public void itemRemoved(ItemEvent<String> itemEvent) {
                LOGGER.trace("PrintApplicationListener removed {}", itemEvent.getItem());
            }
        };
        for (String priorita : queuePriorita) {
            queuePrintApplication(priorita).addItemListener(printApplicationListener, true);			
		}		
	}
}