package it.cnr.si.config;

import java.util.List;

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
    private static final String SIGLA_PRIORITA = "SIGLA_PRIORITA";
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConfiguration.class);    

    @Value("#{'${queue.sigla.priorita}'.split(',')}")
    private List<String> queuePriorita;
    
    @Autowired
    private HazelcastInstance hazelcastInstance;
    
    public IQueue<String> queuePrintApplication(String priorita) {
        return hazelcastInstance.getQueue(SIGLA_PRIORITA.concat(priorita));
    }
    
	@Override
	public void afterPropertiesSet() throws Exception {
		ItemListener<String> printApplicationListener = new ItemListener<String>() {
            @Override
            public void itemAdded(ItemEvent<String> itemEvent) {
            	String priorita = itemEvent.getItem();
                LOGGER.info("PrintApplicationListener {} {}", priorita, itemEvent.getEventType().getType());
                boolean removed = queuePrintApplication(priorita).remove(priorita);
                LOGGER.info("PrintApplicationListener {} {}", priorita, removed ? "removed" : "not removed");
                if (removed) {
                    LOGGER.info("PrintApplicationListener consuming {}", priorita);
                    //printService.print(Integer.valueOf(priorita));
                    LOGGER.info("PrintApplicationListener consumed {}", priorita);
                }
            }
            @Override
            public void itemRemoved(ItemEvent<String> itemEvent) {
                LOGGER.info("PrintApplicationListener removed {}", itemEvent.getItem());
            }
        };
        for (String priorita : queuePriorita) {
            queuePrintApplication(priorita).addItemListener(printApplicationListener, true);			
		}		
	}
    
}
