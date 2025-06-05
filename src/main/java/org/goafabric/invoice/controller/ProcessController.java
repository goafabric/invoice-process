package org.goafabric.invoice.controller;

import org.goafabric.invoice.process.InvoiceProcess;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "processes", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessController {
    private final InvoiceProcess invoiceProcess;

    public ProcessController(InvoiceProcess invoiceProcess) {
        this.invoiceProcess = invoiceProcess;
    }

    @GetMapping("start")
    public String start() {
        invoiceProcess.run();
        return "launched";
    }

    @GetMapping("loop")
    public String loop() throws ExecutionException, InterruptedException {
        for (int i = 0; i < 10; i++) {
            invoiceProcess.run().get();
        }
        return "launched";
    }

}
