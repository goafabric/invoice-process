package org.goafabric.invoice.process.adapter.catalog;

public record Condition (
    String id,
    Long version,
    String code,
    String display,
    String shortname
) {}