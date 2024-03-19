package org.goafabric.invoice.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${test.mode:false}") Boolean testMode;

    @Autowired
    InvoiceProcessFactory invoiceProcessFactory;


    @Override
    public void run(String... args) {
        if ( ((args.length > 0) && ("-check-integrity".equals(args[0]))) || (testMode) ){
            return;
        }
        run();
    }

    boolean run() {
        var invoiceProcess = invoiceProcessFactory.create();

        try {
            invoiceProcess
                    .checkAuthorization()
                    .acquireLock()
                    .retrieveRecords()
                    .checkInvoice()
                    .encryptInvoice()
                    .sendInvoice()
                    .storeInvoice();
        } finally {
            invoiceProcess.releaseLock();
        }

        return true;
    }

}
