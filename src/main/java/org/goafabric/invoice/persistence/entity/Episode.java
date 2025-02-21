package org.goafabric.invoice.persistence.entity;

import java.util.UUID;

public record Episode (
    String id,
    String patientId,
    Integer creationYear,
    Integer creationQuarter
) {
    public Episode(String patientId, Integer creationYear, Integer creationQuarter) {
        this(UUID.randomUUID().toString(), patientId, creationYear, creationQuarter);
    }
}
