package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.process.adapter.invoice.InvoiceMockAdapter;
import org.goafabric.invoice.process.adapter.invoice.dto.Invoice;
import org.goafabric.invoice.process.adapter.s3.S3Adapter;
import org.goafabric.invoice.process.adapter.s3.dto.ObjectEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

@Component
public class InvoiceStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final InvoiceMockAdapter invoiceAdapter;
    private final S3Adapter s3Adapter;

    @Value("${spring.cloud.aws.s3.endpoint:}")
    private String s3Endpoint;

    public InvoiceStep(InvoiceMockAdapter invoiceAdapter, S3Adapter s3Adapter) {
        this.invoiceAdapter = invoiceAdapter;
        this.s3Adapter = s3Adapter;
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
        if (StringUtils.hasText(s3Endpoint)) {
            log.info("storing invoice");
            var objectEntry = new ObjectEntry(
                    "invoice.txt", MediaType.TEXT_PLAIN_VALUE, (long) invoice.content().length(), invoice.content().getBytes(StandardCharsets.UTF_8));
            s3Adapter.save(objectEntry);
        }
    }
}
