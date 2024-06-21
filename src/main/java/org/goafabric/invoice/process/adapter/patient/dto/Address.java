package org.goafabric.invoice.process.adapter.patient.dto;

public record Address (
        String id,
        Long version,

        String use,
        String street,
        String city,
        String postalCode,
        String state,
        String country
) {}

