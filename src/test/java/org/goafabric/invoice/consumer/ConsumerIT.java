package org.goafabric.invoice.consumer;

import org.goafabric.event.EventData;
import org.goafabric.invoice.consumer.config.LatchConsumer;
import org.goafabric.invoice.controller.extensions.UserContext;
import org.goafabric.invoice.persistence.ADTCreator;
import org.goafabric.invoice.persistence.EpisodeDetailsRepository;
import org.goafabric.invoice.persistence.EpisodeRepository;
import org.goafabric.invoice.persistence.entity.Episode;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
import org.goafabric.invoice.util.TestDataCreator;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.goafabric.invoice.util.TestDataCreator.createChargeItems;
import static org.goafabric.invoice.util.TestDataCreator.createConditions;

@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@Import(KafkaAutoConfiguration.class)
class ConsumerIT {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private EpisodeDetailsRepository episodeDetailsRepository;

    @Autowired
    private List<LatchConsumer> consumers;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    void produce() {
        log.info("producing data ...");

        createPatients();
        createConditions().forEach(medicalRecord -> send("condition", "create", medicalRecord.id(), medicalRecord));
        createChargeItems().forEach(medicalRecord -> send("chargeitem", "create", medicalRecord.id(), medicalRecord));

        log.info("consuming data ...");

        consumers.forEach(consumer -> {
            try { assertThat(consumer.getLatch().await(10, TimeUnit.SECONDS)).isTrue();
            } catch (InterruptedException e) { throw new RuntimeException(e);}
        });

        var episodeDetails = episodeDetailsRepository.findAll("1");
        assertThat(episodeDetails).isNotNull().isNotEmpty();

        log.info("logging episode details");
        episodeDetails.forEach(entry -> log.info(entry.toString()));

        log.info("logging adt");
        episodeDetails.forEach(entry -> log.info(ADTCreator.fromEpisodeDetails(entry)));
    }

    private List<Patient> createPatients() {
        var patients = TestDataCreator.createPatients();

        patients.forEach(patient -> episodeRepository.save(new Episode(patient.id(), 2024, 2)));
        patients.forEach(patient -> send("patient", "create", patient.id(), patient));
        var lastPatient = patients.getLast();
        send("patient", "update", lastPatient.id(), new Patient(lastPatient.id(), null, "updated", "updated" ,"u", lastPatient.birthDate(), lastPatient.address(), lastPatient.contactPoint()));
        return patients;
    }

    private void send(String topic, String operation, String referenceId, Object payload) {
        kafkaTemplate.send(topic, referenceId, new EventData(UserContext.getAdapterHeaderMap(), referenceId, operation, payload));
    }

}