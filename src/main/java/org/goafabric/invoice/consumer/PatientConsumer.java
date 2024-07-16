package org.goafabric.invoice.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.goafabric.event.EventData;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static org.goafabric.invoice.consumer.ConsumerUtil.withTenantInfos;

@Component
public class PatientConsumer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    static final String CONSUMER_NAME = "Patient";

    @KafkaListener(groupId = CONSUMER_NAME, topics = "patient")
    public void process(@Header(KafkaHeaders.RECEIVED_TOPIC) String topic, EventData eventData) {
        log.info(topic);
        withTenantInfos(() -> process(eventData));
    }

    private void process(EventData eventData) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Patient patient = objectMapper.convertValue(eventData.payload(), Patient.class);
        log.info(patient.toString());
        //todo: db insert
    }

}
