package org.goafabric.invoice.process.adapter.patient.dto;

public enum MedicalRecordType {

    ANAMNESIS("ANAMNESIS"),
    CONDITION("CONDITION"),
    CHARGEITEM("CHARGE"),
    FINDING("FINDING"),
    THERAPY("THERAPY"),
    BODY_METRICS("BODY_METRICS");
    
    private String value;

    MedicalRecordType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }



}
