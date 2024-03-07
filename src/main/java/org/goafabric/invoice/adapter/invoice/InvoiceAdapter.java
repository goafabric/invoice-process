package org.goafabric.invoice.adapter.invoice;

import org.goafabric.invoice.adapter.invoice.dto.Invoice;

public interface InvoiceAdapter {
    Invoice create();
    void check(Invoice invoice);

    Invoice encrypt(Invoice invoice);

    void send(Invoice invoice);

    void store(Invoice invoice);
}
