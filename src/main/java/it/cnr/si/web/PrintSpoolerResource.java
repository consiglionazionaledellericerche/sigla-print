package it.cnr.si.web;

import it.cnr.si.domain.sigla.PrintSpooler;
import it.cnr.si.service.PrintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/printSpooler")
public class PrintSpoolerResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrintSpoolerResource.class);

    @Autowired
    PrintService printService;

    @GetMapping("/{pg_stampa}")
    public ResponseEntity<PrintSpooler> getPrintSpooler(@PathVariable Long pg_stampa) {
        return new ResponseEntity<>(printService.findPrintSpoolerById(pg_stampa),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Long> createPrintSpooler(@RequestBody PrintSpooler printSpooler,
                                                   @RequestHeader("ds-utente") String userName) {
            return new ResponseEntity<>(printService.createPrintSpooler(printSpooler), HttpStatus.OK);
    }

    @DeleteMapping("/{pg_stampa}")
    public ResponseEntity<String> deletePrintSpooler(@PathVariable Long pg_stampa,
                                                     @RequestHeader("ds-utente") String userName) {

            printService.deleteReport(pg_stampa);
            return ResponseEntity.ok("done");

    }

    @PutMapping("/{pg_stampa}")
    public ResponseEntity<PrintSpooler> updatePrintSpooler(@PathVariable Long pg_stampa,
                                                           @RequestBody PrintSpooler printSpooler,
                                                           @RequestHeader("ds-utente") String userName) {

        return new ResponseEntity<>(printService.updatePrintSpooler(pg_stampa,printSpooler),HttpStatus.OK);

    }

}

