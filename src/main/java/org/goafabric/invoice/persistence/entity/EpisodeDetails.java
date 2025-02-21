package org.goafabric.invoice.persistence.entity;

import java.util.UUID;

public record EpisodeDetails(
        String id,
        String episodeId,
        String referenceId,
        String type,
        String code,
        String display,
        String patientFamily,
        String patientGiven,
        String patientCity,
        String patientStreet
) {
    // Primary constructor is provided by default

    // Secondary constructor that generates a UUID for the id
    public EpisodeDetails(
            String episodeId,
            String referenceId,
            String type,
            String code,
            String display,
            String patientFamily,
            String patientGiven,
            String patientCity,
            String patientStreet
    ) {
        this(UUID.randomUUID().toString(), episodeId, referenceId, type, code, display, patientFamily, patientGiven, patientCity, patientStreet);
    }
}
