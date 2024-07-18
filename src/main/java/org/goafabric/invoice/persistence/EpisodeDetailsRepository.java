package org.goafabric.invoice.persistence;

import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EpisodeDetailsRepository {
    private List<EpisodeDetails> entries = new ArrayList<>();

    public List<EpisodeDetails> findAll() { return Collections.unmodifiableList(entries); }

    public void save(EpisodeDetails entry) {
        if (entries.stream().anyMatch(e -> e.referenceId().equals(entry.referenceId()))) { delete(entry); } //update
        entries.add(entry);
    }

    public void delete(EpisodeDetails entry) { entries.remove(entry); }
}
