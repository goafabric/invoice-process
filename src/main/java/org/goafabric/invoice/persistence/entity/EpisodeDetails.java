package org.goafabric.invoice.persistence.entity;

public record EpisodeDetails(
        String id,
        String episodeId,
        String referenceId,
        String type,
        String code,
        String display,

        //patient could also be stored in seperate table if episode:patient is 1:1
        String patientFamily,
        String patientGiven,
        String patientCity,
        String patientStreet
) {}
