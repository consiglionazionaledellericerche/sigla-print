package it.cnr.si.web;

import it.cnr.si.dto.Commit;
import it.cnr.si.dto.HookRequest;
import it.cnr.si.dto.PrintRequest;
import it.cnr.si.service.PrintService;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.List;
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

    @PostMapping("/api/v1/print")
    public ResponseEntity<byte[]> print(@RequestBody PrintRequest printRequest) {
        LOGGER.info("print request: {}", printRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String fileName = printRequest.getName();
        headers.add("content-disposition", "inline;filename=" +
                fileName);

        String path = printRequest.getPath();
        JasperReport jasperReport = printService.jasperReport(path);

        ByteArrayOutputStream outputStream = printService.print(jasperReport);

        return new ResponseEntity<>(outputStream.toByteArray(),
                headers, HttpStatus.OK);

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
                .forEach(printService::evict);

        return ResponseEntity.ok("done");
    }




}
