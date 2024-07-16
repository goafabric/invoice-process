package org.goafabric.invoice.consumer;

import org.goafabric.event.EventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.goafabric.invoice.consumer.ConsumerUtil.withTenantInfos;

@Component
public class MedicalRecordConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static final String CONSUMER_NAME = "MedicalRecord";

    @KafkaListener(groupId = CONSUMER_NAME, topics = "medicalrecord")
    public void process(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        withTenantInfos(() -> process(eventData));
    }

    private void process(EventData eventData) {
        //var medicalRecord = (MedicalRecord) eventData.payload();
        //log.info(medicalRecord.toString());
        //todo: db insert
    }

}
