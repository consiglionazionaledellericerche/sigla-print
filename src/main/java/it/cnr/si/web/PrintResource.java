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

package it.cnr.si.web;

import it.cnr.si.config.QueueConfiguration;
import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.domain.sigla.PrintState;
import it.cnr.si.dto.Commit;
import it.cnr.si.dto.HookRequest;
import it.cnr.si.dto.EventPrint;
import it.cnr.si.service.CacheService;
import it.cnr.si.service.PrintService;
import it.cnr.si.service.PrintStorageService;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by francesco on 09/09/16.
 */

@RestController
public class PrintResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintResource.class);

    @Autowired
    private PrintService printService;
    @Autowired
    private CacheService cacheService;

    @Autowired
    private PrintStorageService storageService;

    @Value("${file.separator}")
    private String fileSeparator;

    @Autowired
    private QueueConfiguration queueConfiguration;

    private EventPrint validateDsOnBody(PrintSpooler printSpooler){
        //Check that is present the DataSourceParameter and tath it isn't Empty
        String json = Optional.ofNullable(Optional.ofNullable(printSpooler.getParams()).
                orElseThrow(() -> new RuntimeException("The Parems is null")).stream().
                filter(paramO -> paramO.getKey().getNomeParam().equalsIgnoreCase(JRParameter.REPORT_DATA_SOURCE)).
                findFirst().orElseThrow(() -> new RuntimeException("The Report DataSource is null"))).
                filter(e -> (e.getValoreParam() != null && (!e.getValoreParam().isEmpty()))).
                orElseThrow(() -> new RuntimeException("The Report DataSource is empty")).getValoreParam();

        PrintSpooler print =printService.findPrintSpoolerById(printSpooler.getPgStampa());

        if (!PrintState.P.equals(print.getStato()))
            throw new RuntimeException("The report hasn't state "+PrintState.P);

        EventPrint eventPrintDsJson = new EventPrint(print.getPrioritaServer().toString(),print.getPgStampa(),json,true);

        return eventPrintDsJson;
    }

    @PostMapping("/api/v1/get/print/dsOnBody")
    public ResponseEntity<String> printDsOnBody(@RequestBody PrintSpooler printSpooler,@RequestHeader("ds-utente") String userName) {
        EventPrint eventPrintDsJson = validateDsOnBody( printSpooler );
        queueConfiguration.queuePrintApplication(eventPrintDsJson.getPriotita()).add(eventPrintDsJson);
        return ResponseEntity.ok("done");
    }

    @PostMapping("/api/v1/get/print")
    public ResponseEntity<byte[]> print(@RequestBody PrintSpooler printSpooler) {
        LOGGER.info("start print request: {}", printSpooler);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String fileName = printSpooler.getName();
        headers.add("content-disposition", "inline;filename=" +
                fileName);
        final JRFileVirtualizer jrFileVirtualizer = printService.fileVirtualizer();
        ByteArrayOutputStream outputStream = printService.print(
                printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler, jrFileVirtualizer), jrFileVirtualizer);
        LOGGER.info("end print request: {} {}", printSpooler.getReport(), printSpooler.getPgStampa());
        return new ResponseEntity<>(outputStream.toByteArray(),
                headers, HttpStatus.OK);

    }

    @GetMapping("/api/v1/get/print/{user}/{name:.+}")
    public ResponseEntity<byte[]> getpdf(@PathVariable String user, @PathVariable String name) {
        LOGGER.info("get report from user: {} and name: {}", user, name);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        headers.add("content-disposition", "inline;filename=" +
                name);
        String path = Arrays.asList(user, name).stream().collect(Collectors.joining(fileSeparator));
        try {
            InputStream inputStream = storageService.get(path);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            LOGGER.error("Cannot find file: {}", path, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/api/v1/get/print/{user}/{name:.+}")
    public ResponseEntity<byte[]> deletepdf(@PathVariable String user, @PathVariable String name) {
        LOGGER.info("delete report from user: {} and name: {}", user, name);

        String path = Arrays.asList(user, name).stream().collect(Collectors.joining(fileSeparator));
        storageService.delete(path);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/api/v1/get/excel/{name:.+}")
    public ResponseEntity<byte[]> getxls(@RequestParam String user, @RequestParam String file, @RequestParam(required = false) String command, @PathVariable String name) {
        LOGGER.info("get report from user: {} and name: {}", user, file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/xls"));
        headers.add("content-disposition", "inline;filename=" +
                name);
        String path = Arrays.asList(user, file).stream().collect(Collectors.joining(fileSeparator));

        try {
            if (command != null && command.equalsIgnoreCase("delete")) {
                storageService.delete(path);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {

                InputStream inputStream = storageService.get(path);

                byte[] bytes = IOUtils.toByteArray(inputStream);
                inputStream.close();
                return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot find file: {}", path, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/v1/hook")
    public ResponseEntity<String> hook(@RequestBody HookRequest hookRequest) {
        LOGGER.info("hook req: {}", hookRequest);

        LOGGER.info("commits: {}", hookRequest.getCommits().stream().map(Commit::getId).collect(Collectors.toList()));

        Function<Function<Commit, List<String>>, Stream<String>> files =
                (mapper) -> hookRequest.getCommits().stream().map(mapper).flatMap(Collection::stream);

        Stream.of(files.apply(Commit::getRemoved), files.apply(Commit::getAdded), files.apply(Commit::getModified))
                .flatMap(s -> s)
                .distinct()
                .sorted()
                .peek(LOGGER::info)
                .map(map -> fileSeparator.concat(map))
                .forEach(cacheService::evict);

        return ResponseEntity.ok("done");
    }


}
