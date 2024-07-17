package org.goafabric.invoice.persistence.entity;

public record Episode (
    String id,
    String patientId,
    Integer creationYear,
    Integer creationQuarter,
    String[] diagnoses
) {}
