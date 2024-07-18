package org.goafabric.invoice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goafabric.event.EventData;
import org.goafabric.invoice.persistence.EpisodeDetailsRepository;
import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import static org.goafabric.invoice.consumer.config.ConsumerUtil.withTenantInfos;

@Component
public class ChargeItemConsumer implements LatchConsumer {
    static final String CONSUMER_NAME = "invoice-chargeitem";
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final EpisodeDetailsRepository episodeDetailsRepository;

    public ChargeItemConsumer(ObjectMapper objectMapper, EpisodeDetailsRepository episodeDetailsRepository) {
        this.objectMapper = objectMapper;
        this.episodeDetailsRepository = episodeDetailsRepository;
    }

    @KafkaListener(groupId = CONSUMER_NAME, topics = "chargeitem")
    public void process(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        withTenantInfos(() -> process(eventData));
    }

    private void process(EventData eventData) {
        var chargeItem = objectMapper.convertValue(eventData.payload(), MedicalRecord.class);
        log.info("operation {}, id {}, object {}", eventData.operation(), eventData.referenceId(), chargeItem.toString());
        String episodeId = "1";
        episodeDetailsRepository.save(
                new EpisodeDetails(UUID.randomUUID().toString(), episodeId, chargeItem.id(), "chargeitem", chargeItem.code(), chargeItem.display(), null, null, null)
        );
        latch.countDown();
    }

    public CountDownLatch getLatch() { return latch; }
}
