package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.persistence.EpisodeDetailsRepository;
import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.process.adapter.patient.PatientAdapter;
import org.goafabric.invoice.process.adapter.patient.dto.Encounter;
import org.goafabric.invoice.process.adapter.patient.type.MedicalRecordType;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EpisodeStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PatientAdapter    patientAdapter;
    private final EncounterAdapter  encounterAdapter;
    private final ConditionAdapter  conditionAdapter;
    private final EpisodeDetailsRepository episodeDetailsRepository;

    @Value("${adapter.catalogservice.url:}")
    private String catalogServiceUrl;

    @Value("${spring.profiles.active:}")
    private String profiles;

    public EpisodeStep(PatientAdapter patientAdapter, EncounterAdapter encounterAdapter, ConditionAdapter conditionAdapter, EpisodeDetailsRepository episodeDetailsRepository) {
        this.patientAdapter = patientAdapter;
        this.encounterAdapter = encounterAdapter;
        this.conditionAdapter = conditionAdapter;
        this.episodeDetailsRepository = episodeDetailsRepository;
    }

    public void retrieveRecords(String familyName) {
        if (profiles.contains("kafka")) {
            log.info("skipping episode creation, due to kafka consumer");
            return;
        }

        log.info("creating episodes from records");
        var patients = patientAdapter.findPatientNamesByFamilyName(familyName);
        if (!patients.isEmpty()) {
            var patient = patients.getFirst();
            createPatient(patientAdapter.getById(patient.id()));

            var encounters = encounterAdapter.findByPatientIdAndDisplay(patient.id(), "");
            createChargeItems(encounters);
            createConditions(encounters);
        }
    }

    private void createPatient(Patient patient) {
        String episodeId = "1";
        episodeDetailsRepository.save(
                new EpisodeDetails(episodeId, patient.id(), "patient", null, null, patient.familyName(), patient.givenName(), patient.address().getFirst().city(), patient.address().getFirst().street())
        );
    }

    private void createChargeItems(List<Encounter> encounters) {
        log.info("chargeitems");
        encounters.getFirst().medicalRecords().stream()
                .filter(record -> record.type().equals(MedicalRecordType.CHARGEITEM))
                .forEach(chargeItem -> {
                    String episodeId = "1";
                    episodeDetailsRepository.save(
                            new EpisodeDetails(episodeId, chargeItem.id(), "chargeitem", chargeItem.code(), chargeItem.display(), null, null,  null, null)
                    );

                    log.info(chargeItem.toString());
                });
    }

    private void createConditions(List<Encounter> encounters) {
        log.info("conditions");
        encounters.getFirst().medicalRecords().stream()
                .filter(record -> record.type().equals(MedicalRecordType.CONDITION))
                .filter(record -> !record.code().equals("none"))
                .forEach(condition -> {
                    String episodeId = "1";
                    episodeDetailsRepository.save(
                            new EpisodeDetails(episodeId, condition.id(), "condition", condition.code(), condition.display(), null, null, null, null)
                    );
                    log.info(condition.toString());
                    log.info(!catalogServiceUrl.isEmpty() ? conditionAdapter.findByCode(condition.code()).toString() : "");
                });
    }

}
