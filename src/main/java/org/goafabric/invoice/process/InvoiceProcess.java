package org.goafabric.invoice.process;

import org.goafabric.invoice.process.steps.AccessStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.PatientStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class InvoiceProcess {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AccessStep accessStep;
    private final InvoiceStep invoiceStep;
    private final PatientStep patientStep;

    public InvoiceProcess(AccessStep accessStep, InvoiceStep invoiceStep, PatientStep patientStep) {
        this.accessStep = accessStep;
        this.invoiceStep = invoiceStep;
        this.patientStep = patientStep;
    }

    Future<Boolean> run() { return run(true); }

    Future<Boolean> run(boolean virtual) {
        try (var executor = virtual ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newFixedThreadPool(3)) {
            return executor.submit(this::innerLoop);
        }
    }

    private Boolean innerLoop() {
        accessStep.checkAuthorization();
        var lock = accessStep.acquireLock();
        try {
            patientStep.retrieveRecords();
                var invoice = invoiceStep.create();
                    invoiceStep.check(invoice);
                        var encryptedInvoice = invoiceStep.encrypt(invoice);
                            invoiceStep.send(encryptedInvoice);
                                invoiceStep.store(encryptedInvoice);
                                    //Thread.sleep(5000);
        }
        catch (Exception e) {
            log.error("error during process {}", e.getMessage());
        }
        finally {
            accessStep.releaseLock(lock);
        }
        return true;
    }

}
