package org.goafabric.invoice.consumer;

import org.goafabric.event.EventData;
import org.goafabric.invoice.consumer.config.LatchConsumer;
import org.goafabric.invoice.persistence.EpisodeDetailsRepository;
import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.util.concurrent.CountDownLatch;

import static org.goafabric.invoice.consumer.config.ConsumerUtil.withTenantInfos;

@Component
public class ChargeItemConsumer implements LatchConsumer {
    static final String CONSUMER_NAME = "invoice-chargeitem";
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final JsonMapper objectMapper;
    private final EpisodeDetailsRepository episodeDetailsRepository;

    public ChargeItemConsumer(JsonMapper objectMapper, EpisodeDetailsRepository episodeDetailsRepository) {
        this.objectMapper = JsonMapper.builder().build();
        this.episodeDetailsRepository = episodeDetailsRepository;
    }

    @KafkaListener(groupId = CONSUMER_NAME, topics = "chargeitem")
    public void process(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        withTenantInfos(() -> process(eventData));
    }

    private void process(EventData eventData) {
        var chargeItem = objectMapper.convertValue(eventData.payload(), MedicalRecord.class);
        log.info("operation {}, id {}, object {}", eventData.operation(), eventData.referenceId(), chargeItem);
        String episodeId = "1";
        episodeDetailsRepository.save(
                new EpisodeDetails(episodeId, chargeItem.id(), "chargeitem", chargeItem.code(), chargeItem.display(), null, null,  null, null)
        );
        latch.countDown();
    }

    public CountDownLatch getLatch() { return latch; }
}
