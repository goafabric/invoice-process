package org.goafabric.invoice.process.adapter.patient.dto;

import org.goafabric.invoice.process.adapter.patient.type.MedicalRecordType;

public record MedicalRecord(
        String id,
        String encounterId,
        Long version,
        MedicalRecordType type,
        String display,
        String code,
        String specialization) {
    public MedicalRecord(MedicalRecordType type, String display, String code) {
        this(null, null, null, type, display, code, null);
    }

    public MedicalRecord(MedicalRecordType type, String display, String code, String specialization) {
        this(null, null, null, type, display, code, specialization);
    }

}
