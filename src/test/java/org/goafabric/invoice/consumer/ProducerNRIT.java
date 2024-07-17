package org.goafabric.invoice.consumer;

import net.datafaker.Faker;
import org.goafabric.event.EventData;
import org.goafabric.invoice.controller.extensions.TenantContext;
import org.goafabric.invoice.persistence.ADTRepository;
import org.goafabric.invoice.process.adapter.patient.dto.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisabledInAotMode
class ProducerIT {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Autowired
    private ADTRepository adtRepository;

    @Autowired
    private List<LatchConsumer> consumers;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Test
    public void produce() throws InterruptedException {
        log.info("producing data ...");
        createPatients();
        createMedicalRecords();

        log.info("consuming data ...");

        consumers.forEach(consumer -> {
            try { assertThat(consumer.getLatch().await(10, TimeUnit.SECONDS)).isTrue();
            } catch (InterruptedException e) { throw new RuntimeException(e);}
        });

        log.info("logging adt results");
        adtRepository.findAll().forEach(entry -> log.info(entry.toString()));
    }

    private void createPatients() {
        var faker = new Faker();
        int size = 5;
        List<Patient> patients = IntStream.range(0, size)
                .mapToObj(i -> createPatient(faker.name().firstName(), faker.name().lastName(),
                        createAddress(faker.simpsons().location()),
                        createContactPoint("555-520")))
                .toList();

        patients.forEach(patient -> send("patient", "create", patient.id(), patient));
        var lastPatient = patients.getLast();
        send("patient", "update", lastPatient.id(),
                new Patient(lastPatient.id(), null, "updated", "updated" ,"u", lastPatient.birthDate(), lastPatient.address(), lastPatient.contactPoint()));
    }
    
    private void send(String topic, String operation, String referenceId, Object payload) {
        kafkaTemplate.send(topic, referenceId, new EventData(TenantContext.getAdapterHeaderMap(), referenceId, operation, payload));
    }
    
    public static Patient createPatient(String givenName, String familyName, List<Address> addresses, List<ContactPoint> contactPoints) {
        return new Patient(UUID.randomUUID().toString(), 0L, givenName, familyName, "male", LocalDate.of(2020, 1, 8),
                addresses, contactPoints
        );
    }

    public static List<Address> createAddress(String street) {
        return Collections.singletonList(
                new Address(UUID.randomUUID().toString(), 0L,  "home", street, "Springfield " + TenantContext.getTenantId()
                        , "555", "Florida", "US"));
    }

    public static List<ContactPoint> createContactPoint(String phone) {
        return Collections.singletonList(new ContactPoint(UUID.randomUUID().toString(), 0L, "home", "phone", phone));
    }
    
    public void createMedicalRecords() {
        var conditions = Arrays.asList(
            new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CONDITION, "Diabetes mellitus Typ 1", "none", null),
            new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CONDITION, "Adipositas", "E66.00", null),
            new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CONDITION, "Pyromanie", "F63.1", null)
        );

        var chargeitems = Arrays.asList(
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CHARGEITEM, "normal examination", "GOAE1", null),
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CHARGEITEM, "normal examination", "GOAE2", null)
            );

        conditions.forEach(medicalRecord -> send("condition", "create", medicalRecord.id(), medicalRecord));
        chargeitems.forEach(medicalRecord -> send("chargeitem", "create", medicalRecord.id(), medicalRecord));

        send("chargeitem", "update", chargeitems.getLast().id(), chargeitems.getLast());
        send("chargeitem", "delete", chargeitems.getLast().id(), chargeitems.getLast());

    }

}