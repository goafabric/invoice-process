package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.adapter.invoice.InvoiceAdapter;
import org.goafabric.invoice.adapter.invoice.dto.Invoice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InvoiceStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceAdapter invoiceAdapter;

    public InvoiceStep(InvoiceAdapter invoiceAdapter) {
        this.invoiceAdapter = invoiceAdapter;
    }

    public Invoice create() {
        return invoiceAdapter.create();
    }

    public void check(Invoice invoice) {
        invoiceAdapter.check(invoice);
    }

    public Invoice encrypt(Invoice invoice) {
        return invoiceAdapter.encrypt(invoice);
    }

    public void send(Invoice invoice) {
        invoiceAdapter.send(invoice);
    }

    public void store(Invoice invoice) {
        log.info("storing invoice");

    }
}
