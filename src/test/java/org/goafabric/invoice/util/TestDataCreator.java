package org.goafabric.invoice.util;

import org.goafabric.invoice.controller.extensions.UserContext;
import org.goafabric.invoice.process.adapter.patient.dto.*;
import org.goafabric.invoice.process.adapter.patient.type.MedicalRecordType;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestDataCreator {

    public static List<Patient> createPatients() {

        return Arrays.asList(
            createPatient("Homer", "Simpson",
                    createAddress("Evergreen Terrace 702"), createContactPoint("555-520")),
            createPatient("Bart", "Simpson",
                    createAddress("Evergreen Terrace 703"), createContactPoint("555-521")),
            createPatient("Monty", "Burns",
                    createAddress("Evergreen Terrace 703"), createContactPoint("555-720"))
        );

    }


    public static Patient createPatient(String givenName, String familyName, List<Address> addresses, List<ContactPoint> contactPoints) {
        return new Patient(UUID.randomUUID().toString(), 0L, givenName, familyName, "male", LocalDate.of(2020, 1, 8),
                addresses, contactPoints
        );
    }

    public static List<Address> createAddress(String street) {
        return Collections.singletonList(
                new Address(UUID.randomUUID().toString(), 0L,  "home", street, "Springfield " + UserContext.getTenantId()
                        , "555", "Florida", "US"));
    }

    public static List<ContactPoint> createContactPoint(String phone) {
        return Collections.singletonList(new ContactPoint(UUID.randomUUID().toString(), 0L, "home", "phone", phone));
    }


    public static List<MedicalRecord> createChargeItems() {
        var chargeitems = Arrays.asList(
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CHARGEITEM, "normal examination", "GOAE1", null),
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CHARGEITEM, "normal examination", "GOAE2", null)
        );
        return chargeitems;
    }

    public static List<MedicalRecord> createConditions() {
        var conditions = Arrays.asList(
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CONDITION, "Diabetes mellitus Typ 1", "none", null),
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CONDITION, "Adipositas", "E66.00", null),
                new MedicalRecord(UUID.randomUUID().toString(), "42", 0L, MedicalRecordType.CONDITION, "Pyromanie", "F63.1", null)
        );
        return conditions;
    }
}
