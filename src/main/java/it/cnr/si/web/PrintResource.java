package it.cnr.si.web;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.dto.Commit;
import it.cnr.si.dto.HookRequest;
import it.cnr.si.service.CacheService;
import it.cnr.si.service.PrintService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Value("${print.output.dir}")
	private String printOutputDir;

    @Value("${file.separator}")
	private String fileSeparator;

    @PostMapping("/api/v1/get/print")
    public ResponseEntity<byte[]> print(@RequestBody PrintSpooler printSpooler) {
        LOGGER.info("print request: {}", printSpooler);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String fileName = printSpooler.getName();
        headers.add("content-disposition", "inline;filename=" +
                fileName);
        ByteArrayOutputStream outputStream = printService.print(
        		printService.jasperPrint(cacheService.jasperReport(printSpooler.getKey()), printSpooler.getParameters()));

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
        String path = Arrays.asList(printOutputDir, user, name).stream().collect(Collectors.joining(fileSeparator));
        try {
			return new ResponseEntity<>(IOUtils.toByteArray(new FileInputStream(new File(path))),
			        headers, HttpStatus.OK);
		} catch (IOException e) {
			LOGGER.error("Cannot find file: {}", path);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

    }

    @DeleteMapping("/api/v1/get/print/{user}/{name:.+}")
    public ResponseEntity<byte[]> deletepdf(@PathVariable String user, @PathVariable String name) {
        LOGGER.info("delete report from user: {} and name: {}", user, name);

        String path = Arrays.asList(printOutputDir, user, name).stream().collect(Collectors.joining(fileSeparator));
        new File(path).delete();
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @GetMapping("/api/v1/get/excel/{name:.+}")
    public ResponseEntity<byte[]> getxls(@RequestParam String user, @RequestParam String file, @RequestParam(required=false) String command, @PathVariable String name) {
        LOGGER.info("get report from user: {} and name: {}", user, file);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/xls"));
        headers.add("content-disposition", "inline;filename=" +
                name);
        String path = Arrays.asList(printOutputDir, user, file).stream().collect(Collectors.joining(fileSeparator));
        File fileXLS = new File(path);
        try {
        	if (command != null && command.equalsIgnoreCase("delete")) {
        		fileXLS.delete();
        		return new ResponseEntity<>(HttpStatus.OK);
        	} else {
    			return new ResponseEntity<>(IOUtils.toByteArray(new FileInputStream(fileXLS)),
    			        headers, HttpStatus.OK);        		
        	}
		} catch (IOException e) {
			LOGGER.error("Cannot find file: {}", path);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
    }   

    @PostMapping("/api/v1/hook")
    public ResponseEntity<String> hook(@RequestBody HookRequest hookRequest) {
        LOGGER.info("hook req: {}", hookRequest);

        LOGGER.info("commits: {}", hookRequest.getCommits().stream().map(Commit::getId).collect(Collectors.toList()));

        Function<Function<Commit, List<String>>, Stream<String>> files =
                (mapper)  -> hookRequest.getCommits().stream().map(mapper).flatMap(Collection::stream);

        Stream.of(files.apply(Commit::getRemoved), files.apply(Commit::getAdded), files.apply(Commit::getModified))
                .flatMap(s -> s)
                .distinct()
                .sorted()
                .peek(LOGGER::info)
                .forEach(cacheService::evict);

        return ResponseEntity.ok("done");
    }




}
