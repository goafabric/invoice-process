package org.goafabric.invoice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goafabric.event.EventData;
import org.goafabric.invoice.persistence.EpisodeDetailsRepository;
import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
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
public class PatientConsumer implements LatchConsumer {
    static final String CONSUMER_NAME = "invoice-patient";
    private final CountDownLatch latch = new CountDownLatch(1);
    private final Logger log = LoggerFactory.getLogger(this.getClass());


    private final ObjectMapper objectMapper;
    private final org.goafabric.invoice.persistence.EpisodeDetailsRepository episodeDetailsRepository;

    public PatientConsumer(ObjectMapper objectMapper, EpisodeDetailsRepository episodeDetailsRepository) {
        this.objectMapper = objectMapper;
        this.episodeDetailsRepository = episodeDetailsRepository;
    }

    @KafkaListener(groupId = CONSUMER_NAME, topics = "patient")
    public void process(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        withTenantInfos(() -> process(eventData));
    }

    private void process(EventData eventData) {
        Patient patient = objectMapper.convertValue(eventData.payload(), Patient.class);
        log.info("operation {}, id {}, object {}", eventData.operation(), eventData.referenceId(), patient.toString());

        String episodeId = "1";
        episodeDetailsRepository.save(
                new EpisodeDetails(UUID.randomUUID().toString(), episodeId, patient.id(), "patient", null, null, patient.familyName(), patient.givenName(), patient.address().getFirst().city(), patient.address().getFirst().street())
        );

        latch.countDown();
    }

    public CountDownLatch getLatch() { return latch; }

}
