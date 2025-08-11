package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.persistence.ADTCreator;
import org.goafabric.invoice.persistence.EpisodeDetailsRepository;
import org.goafabric.invoice.process.adapter.invoice.InvoiceMockAdapter;
import org.goafabric.invoice.process.adapter.invoice.Invoice;
import org.goafabric.invoice.process.adapter.s3.S3Adapter;
import org.goafabric.invoice.process.adapter.s3.dto.ObjectEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class InvoiceStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final EpisodeDetailsRepository episodeDetailsRepository;
    private final InvoiceMockAdapter invoiceAdapter;
    private final S3Adapter s3Adapter;

    @Value("${spring.cloud.aws.s3.endpoint:}")
    private String s3Endpoint;

    public InvoiceStep(EpisodeDetailsRepository episodeDetailsRepository, InvoiceMockAdapter invoiceAdapter, S3Adapter s3Adapter) {
        this.episodeDetailsRepository = episodeDetailsRepository;
        this.invoiceAdapter = invoiceAdapter;
        this.s3Adapter = s3Adapter;
    }

    public Invoice create() {
        var episodeDetails = episodeDetailsRepository.findAll("1");

        log.info("logging adt");
        final StringBuilder content = new StringBuilder();
        episodeDetails.forEach(entry -> content.append(ADTCreator.fromEpisodeDetails(entry)).append("\n"));

        log.info("\n {}", content);
        return new Invoice(UUID.randomUUID().toString(), content.toString());
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
