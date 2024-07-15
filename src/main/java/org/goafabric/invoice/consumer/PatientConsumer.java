package org.goafabric.invoice.consumer;

import org.goafabric.event.EventData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class PatientConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static final String CONSUMER_NAME = "Patient";

    @KafkaListener(groupId = CONSUMER_NAME, topics = "patient")
    public void processKafka(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        process(topic, eventData);
    }

    private void process(String topic, EventData eventData) {
        log.info(eventData.toString());
    }


}
