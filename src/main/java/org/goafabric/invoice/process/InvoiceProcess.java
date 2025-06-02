package org.goafabric.invoice.process;

import jakarta.annotation.PreDestroy;
import org.goafabric.invoice.process.adapter.authorization.Lock;
import org.goafabric.invoice.process.steps.AuthorizationStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.EpisodeStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class InvoiceProcess {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthorizationStep authorizationStep;
    private final InvoiceStep invoiceStep;
    private final EpisodeStep episodeStep;
    private final ExecutorService executor;

    public InvoiceProcess(AuthorizationStep authorizationStep, InvoiceStep invoiceStep, EpisodeStep episodeStep) {
        this.authorizationStep = authorizationStep;
        this.invoiceStep = invoiceStep;
        this.episodeStep = episodeStep;
        executor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public Future<Boolean> run() {
        return executor.submit(this::innerLoop);
    }

    private Boolean innerLoop() {
        Lock lock = null;
        try {
            lock = authorizationStep.acquireLock();
            episodeStep.retrieveRecords("Burns");
                var invoice = invoiceStep.create();
                    invoiceStep.check(invoice);
                        var encryptedInvoice = invoiceStep.encrypt(invoice);
                            invoiceStep.send(encryptedInvoice);
                                invoiceStep.store(encryptedInvoice);
                                    doSleep();
        }
        catch (Exception e) {
            log.error("error during process: {}", e.getMessage(), e);
            throw e;
        }
        finally {
            authorizationStep.releaseLock(lock);
            log.info("finished ...");
        }
        return true;
    }

    private void doSleep() {
        log.info("sleeping");
        try { Thread.sleep(1000); } catch (InterruptedException e) { // no need for exception
        }
    }

    @PreDestroy
    private void shutdown() {
        executor.shutdown();
    }

}
