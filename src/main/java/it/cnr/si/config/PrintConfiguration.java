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

import it.cnr.si.service.ExcelService;
import it.cnr.si.service.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

    @Autowired
    private PrintService printService;

    @Value("#{'${print.queue.priorita}'.split(',')}")
    private List<String> queuePriorita;


    @Scheduled(fixedDelayString = "${print.scheduler}")
    public void printScheduler() {
        LOGGER.debug("Start scheduler at {}", ZonedDateTime.now());
        for (String priorita : queuePriorita) {
            queueConfiguration.queuePrint(priorita);
        }
        Optional.ofNullable(excelService.print()).map(map -> queueConfiguration.executeExcel(map));
    }

    @Scheduled(cron = "${print.deletecron}")
    public void delete() {
        queueConfiguration.delete();
    }
}