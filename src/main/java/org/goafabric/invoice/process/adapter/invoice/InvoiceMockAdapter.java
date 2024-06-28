package org.goafabric.invoice.process.adapter.invoice;

import org.goafabric.invoice.process.adapter.invoice.dto.Invoice;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Component
@CacheConfig(cacheNames = "invoiceAdapter")
@RegisterReflectionForBinding(Invoice.class)
public class InvoiceMockAdapter  {

    public Invoice create() {
        return new Invoice(UUID.randomUUID().toString(), "Example Content");
    }

    public void check(Invoice invoice) {
        if (invoice.content().isEmpty()) {
            throw new IllegalStateException("Validation failed");
        }
    }

    public Invoice encrypt(Invoice invoice) {
        var encryptedContent = Base64.getEncoder().encodeToString(
                invoice.content().getBytes(StandardCharsets.UTF_8));
        return new Invoice(invoice.id(), encryptedContent);
    }

    public void send(Invoice invoice) {

    }

    @Cacheable
    public Invoice store(Invoice invoice) {
        return invoice;
    }
}
