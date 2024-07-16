package org.goafabric.invoice.persistence.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "condition", schema = "catalog")
public record ConditionEo(
    @Id String id,
    @Version Long version,
    String code,
    String display,
    String shortname
) {
    public ConditionEo(String code, String display, String shortname) {
        this(UUID.randomUUID().toString(), null, code, display, shortname);
    }
}
