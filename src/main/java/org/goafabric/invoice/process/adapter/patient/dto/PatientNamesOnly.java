package org.goafabric.invoice.process.adapter.patient.dto;

public record PatientNamesOnly (
    String id,
    String givenName,
    String familyName
) {}
