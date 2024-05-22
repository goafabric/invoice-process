package org.goafabric.invoice.process.steps;

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

    public PatientStep(PatientAdapter patientAdapter, EncounterAdapter encounterAdapter) {
        this.patientAdapter = patientAdapter;
        this.encounterAdapter = encounterAdapter;
    }

    public void retrieveRecords(String familyName) {
        log.info("retrieve records");
        var patients = patientAdapter.findPatientNamesByFamilyName(familyName);
        if (!patients.isEmpty()) {
            var encounters = encounterAdapter.findByPatientIdAndDisplay(patients.getFirst().id(), "");
            encounters.getFirst().medicalRecords().stream()
                    .filter(record -> record.type().equals(MedicalRecordType.CONDITION))
                    .forEach(m -> log.info(m.toString()));
        }
    }

}
