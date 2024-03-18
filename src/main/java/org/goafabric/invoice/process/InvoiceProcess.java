package org.goafabric.invoice.process;

import org.goafabric.invoice.process.steps.AccessStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.RecordReadStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${test.mode:false}") Boolean testMode;

    private final AccessStep accessStep;

    private final InvoiceStep invoiceStep;

    private final RecordReadStep recordReadStep;


    public InvoiceProcess(AccessStep accessStep, InvoiceStep invoiceStep, RecordReadStep recordReadStep) {
        this.accessStep = accessStep;
        this.invoiceStep = invoiceStep;
        this.recordReadStep = recordReadStep;
    }

    @Override
    public void run(String... args) {
        if ( ((args.length > 0) && ("-check-integrity".equals(args[0]))) || (testMode) ){
            return;
        }

        run();
    }

    boolean run() {
        accessStep.checkAuthorization();
        var lock = accessStep.acquireLock();
        try {
            recordReadStep.retrieveRecords();
                var invoice = invoiceStep.create();
                    invoiceStep.check(invoice);
                        var encryptedInvoice = invoiceStep.encrypt(invoice);
                            invoiceStep.send(encryptedInvoice);
                                invoiceStep.store(encryptedInvoice);
        }
        catch (Exception e) {
            log.error("error during process {}", e.getMessage());
        }
        finally {
            accessStep.removeLock(lock);
        }
        return true;
    }

}
