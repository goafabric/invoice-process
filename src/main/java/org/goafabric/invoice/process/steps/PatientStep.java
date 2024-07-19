package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.process.adapter.patient.PatientAdapter;
import org.goafabric.invoice.process.adapter.patient.dto.Encounter;
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecordType;
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

    @Value("${adapter.catalogservice.url:}")
    private String catalogServiceUrl;

    public PatientStep(PatientAdapter patientAdapter, EncounterAdapter encounterAdapter, ConditionAdapter conditionAdapter) {
        this.patientAdapter = patientAdapter;
        this.encounterAdapter = encounterAdapter;
        this.conditionAdapter = conditionAdapter;
    }

    public void retrieveRecords(String familyName) {
        log.info("retrieve records");
        var patients = patientAdapter.findPatientNamesByFamilyName(familyName);
        if (!patients.isEmpty()) {
            var encounters = encounterAdapter.findByPatientIdAndDisplay(patients.getFirst().id(), "");
            logChargeItems(encounters);
            logConditions(encounters);
        }
    }


    private void logChargeItems(List<Encounter> encounters) {
        log.info("chargeitems");
        encounters.getFirst().medicalRecords().stream()
                .filter(record -> record.type().equals(MedicalRecordType.CHARGEITEM))
                .forEach(chargeItem -> {
                    log.info(chargeItem.toString());
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
