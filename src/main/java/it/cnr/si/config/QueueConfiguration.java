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

package it.cnr.si.config;

import com.hazelcast.core.*;
import it.cnr.si.domain.sigla.ExcelSpooler;
import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintSpoolerParam;
import it.cnr.si.domain.sigla.PrintSpoolerParamKey;
import it.cnr.si.dto.EventPrintDsJson;
import it.cnr.si.service.CacheService;
import it.cnr.si.service.ExcelService;
import it.cnr.si.service.PrintService;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Configuration
public class QueueConfiguration implements InitializingBean {
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

    public IQueue queuePrintApplication(String priorita) {
        return hazelcastInstance.getQueue(SIGLA_PRIORITA.concat(priorita));
    }

    public void queuePrint(String priorita) {
        Optional.ofNullable(printService.print(Integer.valueOf(priorita)))
                .ifPresent(pgStampa -> queuePrintApplication(priorita).add(pgStampa));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (String priorita : queuePriorita) {
            queuePrintApplication(priorita).addItemListener(new PrintApplicationListener(priorita), true);
        }
    }

    public Long executeExcel(ExcelSpooler excelSpooler) {
        String lockKey = XLS.concat(String.valueOf(excelSpooler.getPgEstrazione()));
        ILock lock = hazelcastInstance.getLock(lockKey);
        try {
            LOGGER.info("try lock {}", lockKey);
            if (lock.tryLock(1, TimeUnit.MILLISECONDS)) {
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
            if (lock.tryLock(1, TimeUnit.SECONDS)) {
                try {
                    printService.deleteReport();
                    excelService.deleteXls();
                } finally {
                    LOGGER.info("unlocking {}", DELETEFILE);
                    lock.unlock();
                }
            } else {
                LOGGER.info("unable to get lock {}", DELETEFILE);
            }
        } catch (InterruptedException e) {
        }
    }

    class PrintApplicationListener implements ItemListener<Object> {
        private final String priorita;

        public PrintApplicationListener(String priorita) {
            this.priorita = priorita;
        }

        private Long getPgStampa( ItemEvent itemEvent ){
            if ( itemEvent.getItem() instanceof EventPrintDsJson)
                return  Optional.ofNullable(itemEvent.getItem()).
                        map(EventPrintDsJson.class::cast).get().getPg_stampa();

             return  Optional.ofNullable(itemEvent.getItem()).
                     filter(Long.class::isInstance).
                     map(Long.class::cast).get();
        }

        private PrintSpooler getPrinSpooler( Long pgStampa,  ItemEvent itemEvent){
            PrintSpooler print = printService.print(pgStampa);
            if ( itemEvent.getItem() instanceof EventPrintDsJson){
                EventPrintDsJson eventPrintDsJson = (EventPrintDsJson) itemEvent.getItem();
                Set<PrintSpoolerParam> params= print.getParams();
                 if ( params==null ) {
                     params = new HashSet<PrintSpoolerParam>();
                     print.setParams( params);
                 }
                   params.removeIf(e->e.getKey().getNomeParam().equalsIgnoreCase(JRParameter.REPORT_DATA_SOURCE));
                   PrintSpoolerParam param = new PrintSpoolerParam(
                           new PrintSpoolerParamKey(JRParameter.REPORT_DATA_SOURCE,print)
                   );
                   param.setValoreParam( eventPrintDsJson.getJson());
                   params.add(param);

            }
            return print;
        }
        @Override
        public void itemAdded(ItemEvent itemEvent) {
            Long pgStampa = getPgStampa( itemEvent);
            LOGGER.debug("PrintApplicationListener {} {}", priorita, itemEvent.getEventType().getType());
            boolean removed = queuePrintApplication(priorita).remove(itemEvent.getItem());
            LOGGER.info("PrintApplicationListener {} {}", pgStampa, removed ? "removed" : "not removed");
            if (removed) {
                LOGGER.trace("PrintApplicationListener consuming {}", priorita);
                PrintSpooler print = null;
                if (pgStampa != null) {
                    String lockKey = PDF.concat(String.valueOf(pgStampa));
                    ILock lock = hazelcastInstance.getLock(lockKey);
                    LOGGER.info("try lock {}", lockKey);
                    try {
                        if (lock.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                //print = printService.print(pgStampa);
                                print =getPrinSpooler(pgStampa,itemEvent);
                                if (Optional.ofNullable(print).isPresent()) {
                                    final JRFileVirtualizer jrFileVirtualizer = printService.fileVirtualizer();
                                        JasperPrint jasperPrint = printService.jasperPrint(cacheService.jasperReport(print.getKey()), print, jrFileVirtualizer);
                                    printService.executeReport(jasperPrint, jrFileVirtualizer, print.getPgStampa(),
                                            print.getName(),
                                            print.getUtcr());
                                }
                            } catch (Throwable _ex) {
                                if (print != null)
                                    printService.error(print, _ex);
                            } finally {
                                LOGGER.info("unlocking {}", lockKey);
                                lock.unlock();
                            }
                        } else {
                            LOGGER.warn("unable to get lock {}", lockKey);
                        }
                    } catch (InterruptedException e) {
                        LOGGER.warn("InterruptedException to get lock {}", lockKey);
                    }
                }
                LOGGER.trace("PrintApplicationListener consumed {}", priorita);
            }
        }

        @Override
        public void itemRemoved(ItemEvent<Object> itemEvent) {
            LOGGER.trace("PrintApplicationListener removed {}", itemEvent.getItem());
        }
    }
}