package org.goafabric.invoice.process;

import jakarta.annotation.PreDestroy;
import org.goafabric.invoice.process.adapter.authorization.dto.Lock;
import org.goafabric.invoice.process.steps.AuthorizationStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.PatientStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

@Component
public class InvoiceProcess {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AuthorizationStep authorizationStep;
    private final InvoiceStep invoiceStep;
    private final PatientStep patientStep;
    private final ExecutorService executor;

    public InvoiceProcess(AuthorizationStep authorizationStep, InvoiceStep invoiceStep, PatientStep patientStep) {
        this.authorizationStep = authorizationStep;
        this.invoiceStep = invoiceStep;
        this.patientStep = patientStep;
        executor = Executors.newFixedThreadPool(3);
    }

    public Future<Boolean> run() {
        return executor.submit(this::innerLoop);
    }

    private Boolean innerLoop() {
        Lock lock = null;
        try {
            lock = authorizationStep.acquireLock();
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
            log.error("error during process: {}", e.getMessage(), e);
            throw e;
        }
        finally {
            authorizationStep.releaseLock(lock);
            log.info("finished ...");
        }
        return true;
    }

    @Autowired
    private CacheManager cacheManager;
    public Future<Boolean> load(Integer range) {
        return executor.submit(() -> {
            try {
                IntStream.range(0, range).forEach(i -> {
                    patientStep.retrieveRecords("Burns");
                    cacheManager.getCacheNames().forEach(name -> cacheManager.getCache(name).clear());
                });
            } catch (Exception e) {
                log.error("error during process: {}", e.getMessage(), e);
                throw e;
            }
            return true;
        });
    }
    @PreDestroy
    private void shutdown() {
        executor.shutdown();
    }

}
