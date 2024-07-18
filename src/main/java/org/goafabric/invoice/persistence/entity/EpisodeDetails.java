package org.goafabric.invoice.persistence.entity;

public record EpisodeDetails(
        String id,
        String episodeId,
        String referenceId,
        String type,
        String code,
        String display,
        String patientName,
        String city,
        String street
) {}
