package org.goafabric.invoice.process.steps

import org.goafabric.invoice.process.adapter.invoice.InvoiceMockAdapter
import org.goafabric.invoice.process.adapter.invoice.dto.Invoice
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class InvoiceStep(private val invoiceAdapter: InvoiceMockAdapter) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun create(): Invoice {
        return invoiceAdapter.create()
    }

    fun check(invoice: Invoice) {
        invoiceAdapter.check(invoice)
    }

    fun encrypt(invoice: Invoice): Invoice {
        return invoiceAdapter.encrypt(invoice)
    }

    fun send(invoice: Invoice) {
        invoiceAdapter.send(invoice)
    }

    fun store(invoice: Invoice) {
        log.info("storing invoice")
    }
}
