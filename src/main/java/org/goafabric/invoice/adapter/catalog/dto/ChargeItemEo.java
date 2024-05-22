package org.goafabric.invoice.adapter.catalog.dto;

public record ChargeItemEo(
    String id,
    Long version,
    String code,
    String display,
    Double price
) {}
