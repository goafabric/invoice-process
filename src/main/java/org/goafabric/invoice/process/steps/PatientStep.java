package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.process.adapter.catalog.ChargeItemAdapter;
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.process.adapter.patient.PatientAdapter;
import org.goafabric.invoice.process.adapter.patient.dto.Encounter;
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecordType;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PatientStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PatientAdapter    patientAdapter;
    private final EncounterAdapter  encounterAdapter;
    private final ConditionAdapter  conditionAdapter;
    private final ChargeItemAdapter chargeItemAdapter;

    @Value("${adapter.catalogservice.url:}")
    private String catalogServiceUrl;

    public PatientStep(PatientAdapter patientAdapter, EncounterAdapter encounterAdapter, ChargeItemAdapter chargeItemAdapter, ConditionAdapter conditionAdapter) {
        this.patientAdapter = patientAdapter;
        this.encounterAdapter = encounterAdapter;
        this.chargeItemAdapter = chargeItemAdapter;
        this.conditionAdapter = conditionAdapter;
    }

    public String retrieveRecords(String familyName) {
        log.info("retrieve records");
        var patients = patientAdapter.findPatientNamesByFamilyName(familyName);
        if (!patients.isEmpty()) {
            var encounters = encounterAdapter.findByPatientIdAndDisplay(patients.getFirst().id(), "");
            logChargeItems(encounters);
            logConditions(encounters);
        }
        return !patients.isEmpty() ? patients.getFirst().id() : null;
    }

    public Patient getPatient(String id) { return patientAdapter.getById(id); }
    public void updatePatient(Patient patient) { patientAdapter.save(patient); }

    private void logChargeItems(List<Encounter> encounters) {
        log.info("chargeitems");
        encounters.getFirst().medicalRecords().stream()
                .filter(record -> record.type().equals(MedicalRecordType.CHARGEITEM))
                .forEach(chargeItem -> {
                    log.info(chargeItem.toString());
                    log.info(!catalogServiceUrl.isEmpty() ? chargeItemAdapter.findByCode(chargeItem.code()).toString() : "");
                });
    }

    private void logConditions(List<Encounter> encounters) {
        log.info("conditions");
        encounters.getFirst().medicalRecords().stream()
                .filter(record -> record.type().equals(MedicalRecordType.CONDITION))
                .filter(record -> !record.code().equals("none"))
                .forEach(condition -> {
                    log.info(condition.toString());
                    log.info(!catalogServiceUrl.isEmpty() ? conditionAdapter.findByCode(condition.code()).toString() : "");
                });
    }

}
