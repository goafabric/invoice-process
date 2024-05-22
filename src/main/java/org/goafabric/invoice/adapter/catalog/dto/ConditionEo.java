package org.goafabric.invoice.adapter.catalog.dto;

public record ConditionEo (
    String id,
    Long version,
    String code,
    String display,
    String shortname
) {}