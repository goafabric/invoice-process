package org.goafabric.invoice.adapter.patient.dto;

import java.time.LocalDate;
import java.util.List;

public record Encounter(
    String id,
    Long version,
    String patientId,
    String practitionerId,
    LocalDate encounterDate,
    String encounterName,
    List<MedicalRecord> medicalRecords
) {}
