package org.goafabric.invoice.process.adapter.invoice;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
@RegisterReflectionForBinding(Invoice.class)
public class InvoiceMockAdapter  {

    public void check(Invoice invoice) {
    }

    public Invoice encrypt(Invoice invoice) {
        var encryptedContent = Base64.getEncoder().encodeToString(
                invoice.content().getBytes(StandardCharsets.UTF_8));
        return new Invoice(invoice.id(), encryptedContent);
    }

    public void send(Invoice invoice) {
        // just empty
    }

    public Invoice store(Invoice invoice) {
        return invoice;
    }
}
