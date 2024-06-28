package org.goafabric.invoice.controller;

import org.goafabric.invoice.process.InvoiceProcess;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.IntStream;

@RestController
@RequestMapping(value = "processes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {
    private final InvoiceProcess invoiceProcess;

    public ProcessController(InvoiceProcess invoiceProcess) {
        this.invoiceProcess = invoiceProcess;
    }

    @GetMapping("start")
    //@RolesAllowed("INVOICE")
    public String start() {
        invoiceProcess.run();
        return "launched";
    }

    @GetMapping("loop")
    //@RolesAllowed("INVOICE")
    public String loop() {
        IntStream.range(0, 10).forEach(i -> {
            try { invoiceProcess.run().get(); } catch (Exception e) { throw new RuntimeException(e);}
        });
        return "launched";
    }

}
