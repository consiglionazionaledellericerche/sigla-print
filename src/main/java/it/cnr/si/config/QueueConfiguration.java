package it.cnr.si.config;

import it.cnr.si.domain.sigla.ExcelSpooler;
import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.service.CacheService;
import it.cnr.si.service.ExcelService;
import it.cnr.si.service.PrintService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.sf.jasperreports.engine.JasperPrint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ItemEvent;
import com.hazelcast.core.ItemListener;

@Configuration
public class QueueConfiguration implements InitializingBean{
    private static final String DELETEFILE = "DELETEFILE";
	private static final String PDF = "PDF_";
	private static final String XLS = "XLS_";
	private static final String SIGLA_PRIORITA = "SIGLA_PRIORITA_";
    private static final Logger LOGGER = LoggerFactory.getLogger(QueueConfiguration.class);    

    @Value("#{'${print.queue.priorita}'.split(',')}")
    private List<String> queuePriorita;
    
    @Autowired
    private HazelcastInstance hazelcastInstance;
    
    @Autowired
    private PrintService printService;

    @Autowired
    private ExcelService excelService;

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
                    Long pgStampa = printService.print(Integer.valueOf(priorita));
                    PrintSpooler print = null;
                    if (pgStampa != null) {                    	
                    	String lockKey = PDF.concat(String.valueOf(pgStampa));
                    	ILock lock = hazelcastInstance.getLock(lockKey);
                    	LOGGER.info("try lock {}", lockKey);	
                    	try {
							if (lock.tryLock ( 2, TimeUnit.SECONDS ) ) {  
								try {
							    		print = printService.print(pgStampa);
							    		JasperPrint jasperPrint = printService.jasperPrint(cacheService.jasperReport(print.getKey()), print);
							        	printService.executeReport(jasperPrint, print.getPgStampa(), 
							        			print.getName(), 
							        			print.getUtcr());
								} catch (Exception _ex) {
									if (print != null)
										printService.error(print, _ex);
								} finally {
							        LOGGER.info("unlocking {}", lockKey);
									lock.unlock();                    		
								}
							} else {
								LOGGER.info("unable to get lock {}", lockKey);				
							}
						} catch (InterruptedException e) {
							//Nothing to do
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
	public Long executeExcel(ExcelSpooler excelSpooler) {
    	String lockKey = XLS.concat(String.valueOf(excelSpooler.getPgEstrazione()));
    	ILock lock = hazelcastInstance.getLock(lockKey);
		try {
        	LOGGER.info("try lock {}", lockKey);			
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {
				try {
					return excelService.executeExcel(excelSpooler);					
				} finally {
		            LOGGER.info("unlocking {}", lockKey);
					lock.unlock();
				}
			} else {
				LOGGER.info("unable to get lock {}", lockKey);				
			}
			return null;
		} catch (InterruptedException e) {
			//Nothing to do
			return null;
		}
	}

	public void delete() {
    	ILock lock = hazelcastInstance.getLock(DELETEFILE);
		try {
        	LOGGER.info("try lock {}", DELETEFILE);				
			if ( lock.tryLock ( 2, TimeUnit.SECONDS ) ) {
				try {
					printService.deleteReport();
			    	excelService.deleteXls();
				}finally {
		            LOGGER.info("unlocking {}", DELETEFILE);
					lock.unlock();                    					
				}
			} else {
				LOGGER.info("unable to get lock {}", DELETEFILE);
			}
		} catch (InterruptedException e) {
		} 
	}	
}