package org.goafabric.invoice.adapter.patient.dto;

public record PatientNamesOnly (
    String id,
    String givenName,
    String familyName
) {}
