package org.goafabric.invoice.process

import jakarta.annotation.PreDestroy
import org.goafabric.invoice.process.adapter.authorization.dto.Lock
import org.goafabric.invoice.process.steps.AuthorizationStep
import org.goafabric.invoice.process.steps.InvoiceStep
import org.goafabric.invoice.process.steps.PatientStep
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Component
class InvoiceProcess(
    private val authorizationStep: AuthorizationStep,
    private val invoiceStep: InvoiceStep,
    private val patientStep: PatientStep
) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    private val executor: ExecutorService = Executors.newFixedThreadPool(3)

    fun run(): Future<Boolean> {
        return executor.submit<Boolean> { this.innerLoop() }
    }

    private fun innerLoop(): Boolean {
        var lock: Lock? = null
        try {
            lock = authorizationStep.acquireLock()
            patientStep.retrieveRecords("Burns")
            val invoice = invoiceStep.create()
            invoiceStep.check(invoice)
            val encryptedInvoice = invoiceStep.encrypt(invoice)
            invoiceStep.send(encryptedInvoice)
            invoiceStep.store(encryptedInvoice)
            log.info("sleeping")
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
            }
        } catch (e: Exception) {
            log.error("error during process: {}", e.message)
            throw e
        } finally {
            authorizationStep.releaseLock(lock)
            log.info("finished ...")
        }
        return true
    }

    @PreDestroy
    private fun shutdown() {
        executor.shutdown()
    }
}
