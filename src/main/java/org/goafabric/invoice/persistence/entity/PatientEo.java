package org.goafabric.invoice.persistence.entity;

import java.time.LocalDate;


public record PatientEo(
    String id,
    Long version,
    String givenName,
    String familyName,

    String gender,
    LocalDate birthDate
) {}
