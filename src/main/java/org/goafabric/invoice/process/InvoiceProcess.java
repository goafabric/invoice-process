package org.goafabric.invoice.process;

import jakarta.annotation.PreDestroy;
import org.goafabric.invoice.controller.extensions.UserContext;
import org.goafabric.invoice.process.adapter.authorization.Lock;
import org.goafabric.invoice.process.steps.AuthorizationStep;
import org.goafabric.invoice.process.steps.EpisodeStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
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
        var userContextMap = UserContext.getAdapterHeaderMap();
        return executor.submit(() -> {
            try {
                return innerLoop(userContextMap);
            } catch (Exception e) {
                //Todo this is just a mitigation of lost exceptions in threads, they are still only logged but not handled by main exception handler - @CircuitBreaker inside the submit block seem to work though
                log.error(e.getMessage(), e);
                throw e;
            }
        });
    }

    /*
    @Async //Also needs @EnableAsync, Exceptionhandling not possibe here, beeds Impl of AsyncUncaughtExceptionHandler
    public Future<Boolean> run() {
        try { innerLoop(UserContext.getAdapterHeaderMap()); } catch (InterruptedException e) { throw new RuntimeException(e);}
        return CompletableFuture.completedFuture(Boolean.TRUE);
    }
    */

    private Boolean innerLoop(Map<String, String> userContextMap) throws InterruptedException {
        UserContext.setContext(userContextMap);
        log.info("##tenantid inside thread {} ", UserContext.getTenantId());
        //if (true) { throw new IllegalStateException("yo baby"); }
        Lock lock = null;
        try {
            lock = authorizationStep.acquireLock();
            episodeStep.retrieveRecords("Burns");
                var invoice = invoiceStep.create();
                    invoiceStep.check(invoice);
                        var encryptedInvoice = invoiceStep.encrypt(invoice);
                            invoiceStep.send(encryptedInvoice);
                                invoiceStep.store(encryptedInvoice);
        }
        finally {
            authorizationStep.releaseLock(lock);
            log.info("finished ...");
        }
        doSleep();
        return true;
    }

    private void doSleep() throws InterruptedException {
        log.info("sleeping");
        Thread.sleep(1000);
    }

    @PreDestroy
    private void shutdown() {
        executor.shutdown();
    }

}
