package org.goafabric.invoice.persistence;

import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EpisodeDetailsRepository {
    private List<EpisodeDetails> entries = new ArrayList<>();

    public List<EpisodeDetails> findAll(String episodeId) {
        return entries.stream().filter(entry -> entry.episodeId().equals(episodeId)).toList();
    }

    public void save(EpisodeDetails entry) {
        if (entries.stream().anyMatch(e -> e.referenceId().equals(entry.referenceId()))) { delete(entry); } //update
        entries.add(entry);
    }

    public void delete(EpisodeDetails entry) { entries.remove(entry); }
}
