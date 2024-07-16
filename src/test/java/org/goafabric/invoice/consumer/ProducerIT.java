package org.goafabric.invoice.consumer;

import net.datafaker.Faker;
import org.goafabric.event.EventData;
import org.goafabric.invoice.controller.extensions.TenantContext;
import org.goafabric.invoice.process.adapter.patient.dto.Address;
import org.goafabric.invoice.process.adapter.patient.dto.ContactPoint;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
import org.goafabric.invoice.process.adapter.s3.S3Adapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
@DisabledInAotMode
class ProducerIT {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @MockBean
    private S3Adapter s3Adapter;

    @Test
    public void produce() {
        createPatients();
    }

    private void createPatients() {
        var faker = new Faker();
        int size = 10;
        IntStream.range(0, size).forEach(i -> {
                    var patient = createPatient(faker.name().firstName(), faker.name().lastName(),
                            createAddress(faker.simpsons().location()),
                            createContactPoint("555-520"));
                    send("patient", "create", patient.id(), patient);
                }
        );
    }
    private void send(String topic, String operation, String referenceId, Object payload) {
        kafkaTemplate.send(topic, referenceId, new EventData(TenantContext.getAdapterHeaderMap(), referenceId, operation, payload));
    }


    public static Patient createPatient(String givenName, String familyName, List<Address> addresses, List<ContactPoint> contactPoints) {
        return new Patient(null, null, givenName, familyName, "male", LocalDate.of(2020, 1, 8),
                addresses, contactPoints
        );
    }

    public static List<Address> createAddress(String street) {
        return Collections.singletonList(
                new Address(null, null,  "home", street, "Springfield " + TenantContext.getTenantId()
                        , "555", "Florida", "US"));
    }

    public static List<ContactPoint> createContactPoint(String phone) {
        return Collections.singletonList(new ContactPoint(null, null, "home", "phone", phone));
    }

}