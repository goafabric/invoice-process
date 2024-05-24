package org.goafabric.invoice.process;

import jakarta.annotation.PreDestroy;
import org.goafabric.invoice.adapter.access.dto.Lock;
import org.goafabric.invoice.process.steps.AccessStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.PatientStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class InvoiceProcess {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AccessStep accessStep;
    private final InvoiceStep invoiceStep;
    private final PatientStep patientStep;
    private final ExecutorService executor;

    public InvoiceProcess(AccessStep accessStep, InvoiceStep invoiceStep, PatientStep patientStep) {
        this.accessStep = accessStep;
        this.invoiceStep = invoiceStep;
        this.patientStep = patientStep;

        boolean virtual = false;
        executor = virtual ? Executors.newVirtualThreadPerTaskExecutor() : Executors.newFixedThreadPool(3);
    }

    public Future<Boolean> run() { return run(true); }

    private Future<Boolean> run(boolean virtual) {
        return executor.submit(this::innerLoop);
    }

    private Boolean innerLoop() {
        Lock lock = null;
        try {
            //accessStep.checkAuthorization();
            lock = accessStep.acquireLock();
            patientStep.retrieveRecords("Burns");
                var invoice = invoiceStep.create();
                    invoiceStep.check(invoice);
                        var encryptedInvoice = invoiceStep.encrypt(invoice);
                            invoiceStep.send(encryptedInvoice);
                                invoiceStep.store(encryptedInvoice);
                                    log.info("sleeping");
                                    try { Thread.sleep(1000); } catch (InterruptedException e) {}
        }
        catch (Exception e) {
            log.error("error during process: {}", e.getMessage());
            throw e;
        }
        finally {
            accessStep.releaseLock(lock);
            log.info("finished ...");
        }
        return true;
    }

    @PreDestroy
    private void shutdown() {
        executor.shutdown();
    }

}
