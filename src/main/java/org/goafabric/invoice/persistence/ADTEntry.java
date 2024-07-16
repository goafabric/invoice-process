package org.goafabric.invoice.persistence;

import java.util.UUID;

public record ADTEntry(
    String id,
    String entryType,
    String entryId,
    String entry
) {
    public ADTEntry(String entryType, String entryId, String entry) {
        this(UUID.randomUUID().toString(), entryType, entryId, entry);
    }
}

