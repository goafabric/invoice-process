package org.goafabric.invoice.persistence;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ADTRepository {
    private List<ADTEntry> adtEntries = new ArrayList<>();

    public List<ADTEntry> findAll() { return Collections.unmodifiableList(adtEntries); }

    public void save(ADTEntry entry) {
        if (adtEntries.stream().anyMatch(e -> e.entryId().equals(entry.entryId()))) { delete(entry); } //update
        adtEntries.add(entry);
    }

    public void delete(ADTEntry entry) { adtEntries.remove(entry); }
}
