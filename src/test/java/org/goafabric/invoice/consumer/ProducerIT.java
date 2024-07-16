package org.goafabric.invoice.consumer;

import net.datafaker.Faker;
import org.goafabric.event.EventData;
import org.goafabric.invoice.controller.extensions.TenantContext;
import org.goafabric.invoice.process.adapter.patient.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@DisabledInAotMode
class ProducerIT {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Test
    public void produce() {
        createPatients();
        createMedicalRecords();
    }

    private void createPatients() {
        var faker = new Faker();
        int size = 10;
        IntStream.range(0, size).forEach(i -> {
                    var patient = createPatient(faker.name().firstName(), faker.name().lastName(),
                            createAddress(faker.simpsons().location()),
                            createContactPoint("555-520"));
                    send("patient", i < 10 ? "create" : "delete", patient.id(), patient);

                }
        );
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