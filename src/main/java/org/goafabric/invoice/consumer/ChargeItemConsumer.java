package org.goafabric.invoice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.goafabric.event.EventData;
import org.goafabric.invoice.persistence.ADTEntry;
import org.goafabric.invoice.persistence.ADTRepository;
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.goafabric.invoice.consumer.config.ConsumerUtil.withTenantInfos;

@Component
public class ChargeItemConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    static final String CONSUMER_NAME = "chargeitem";

    private final ObjectMapper objectMapper;
    private final ADTRepository adtRepository;

    public ChargeItemConsumer(ObjectMapper objectMapper, ADTRepository adtRepository) {
        this.objectMapper = objectMapper;
        this.adtRepository = adtRepository;
    }

    @KafkaListener(groupId = CONSUMER_NAME, topics = "chargeitem")
    public void process(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        withTenantInfos(() -> process(eventData));
    }

    private void process(EventData eventData) {
        var chargeItem = objectMapper.convertValue(eventData.payload(), MedicalRecord.class);
        log.info("operation {}, id {}, object {}", eventData.operation(), eventData.referenceId(), chargeItem.toString());
        adtRepository.save(new ADTEntry("chargeitem", chargeItem.id(),
                "FT1|1|" + "I10|" + chargeItem.code() + "^" + chargeItem.display()));
    }


}
