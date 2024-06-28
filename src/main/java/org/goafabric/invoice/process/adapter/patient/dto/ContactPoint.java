package org.goafabric.invoice.process.adapter.patient.dto;

public record ContactPoint (
    String id,
    Long version,

    String use,
    String system,
    String value
) {}
