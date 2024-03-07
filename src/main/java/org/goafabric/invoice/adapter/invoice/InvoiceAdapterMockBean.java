package org.goafabric.invoice.adapter.invoice;

import org.goafabric.invoice.adapter.invoice.dto.Invoice;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
public class InvoiceAdapterMockBean implements InvoiceAdapter {

    @Override
    public Invoice create() {
        return new Invoice(UUID.randomUUID().toString(), "Example Content");
    }

    @Override
    public void check(Invoice invoice) {
        if (invoice.content().isEmpty()) {
            throw new IllegalStateException("Validation failed");
        }
    }

    @Override
    public Invoice encrypt(Invoice invoice) {
        var encryptedContent = Base64.getEncoder().encodeToString(
                invoice.content().getBytes(StandardCharsets.UTF_8));
        return new Invoice(invoice.id(), encryptedContent);
    }

    @Override
    public void send(Invoice invoice) {

    }

    @Override
    public void store(Invoice invoice) {

    }
}
