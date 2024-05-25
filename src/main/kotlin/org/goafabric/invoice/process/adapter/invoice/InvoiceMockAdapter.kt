package org.goafabric.invoice.process.adapter.invoice

import org.goafabric.invoice.process.adapter.invoice.dto.Invoice
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*

@Component
class InvoiceMockAdapter {
    fun create(): Invoice {
        return Invoice(UUID.randomUUID().toString(), "Example Content")
    }

    fun check(invoice: Invoice) {
        check(!invoice.content.isEmpty()) { "Validation failed" }
    }

    fun encrypt(invoice: Invoice): Invoice {
        val encryptedContent = Base64.getEncoder().encodeToString(
            invoice.content.toByteArray(StandardCharsets.UTF_8)
        )
        return Invoice(invoice.id, encryptedContent)
    }

    fun send(invoice: Invoice?) {
    }

    fun store(invoice: Invoice?) {
    }
}
