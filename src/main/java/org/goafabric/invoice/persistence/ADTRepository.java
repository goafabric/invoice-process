package org.goafabric.invoice.persistence;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ADTRepository {
    private List<ADTEntry> adtEntries = new ArrayList<>();

    public List<ADTEntry> findAll() {
        return Collections.unmodifiableList(adtEntries);
    }

    public void save(ADTEntry entry) {
        var foundEntry = adtEntries.stream().filter(e -> e.entryId().equals(entry.entryId())).findFirst();
        if (foundEntry.isPresent()) {
            delete(entry);
        }
        adtEntries.add(entry);
    }

    public void delete(ADTEntry entry) {
        adtEntries.remove(entry);
    }

}
