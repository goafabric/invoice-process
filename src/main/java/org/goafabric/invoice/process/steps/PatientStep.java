package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.adapter.catalog.ChargeItemAdapter;
import org.goafabric.invoice.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.adapter.patient.PatientAdapter;
import org.goafabric.invoice.adapter.patient.dto.MedicalRecordType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PatientStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PatientAdapter patientAdapter;

    private final EncounterAdapter encounterAdapter;

    private final ChargeItemAdapter chargeItemAdapter;

    private final ConditionAdapter conditionAdapter;

    public PatientStep(PatientAdapter patientAdapter, EncounterAdapter encounterAdapter, ChargeItemAdapter chargeItemAdapter, ConditionAdapter conditionAdapter) {
        this.patientAdapter = patientAdapter;
        this.encounterAdapter = encounterAdapter;
        this.chargeItemAdapter = chargeItemAdapter;
        this.conditionAdapter = conditionAdapter;
    }

    public void retrieveRecords(String familyName) {
        log.info("retrieve records");
        var patients = patientAdapter.findPatientNamesByFamilyName(familyName);
        if (!patients.isEmpty()) {
            var encounters = encounterAdapter.findByPatientIdAndDisplay(patients.getFirst().id(), "");
            encounters.getFirst().medicalRecords().stream()
                    .filter(record -> record.type().equals(MedicalRecordType.CHARGEITEM))
                    .forEach(chargeItem -> {
                        log.info(chargeItem.toString());
                        log.info(chargeItemAdapter.findByDisplay(chargeItem.code()).toString());
                    });
        }
    }

}
