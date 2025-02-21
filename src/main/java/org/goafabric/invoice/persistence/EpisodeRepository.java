package org.goafabric.invoice.persistence;

import org.goafabric.invoice.persistence.entity.Episode;
import org.goafabric.invoice.persistence.entity.EpisodeDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EpisodeRepository {
    private List<Episode> episodes = new ArrayList<>();

    public List<Episode> findAll() {
        return episodes;
    }

    public List<Episode> findByPatientId(String patientId) {
        return episodes.stream().filter(entry -> entry.patientId().equals(patientId)).toList();
    }

    public void save(Episode episode) {
        episodes.add(episode);
    }

    public void delete(EpisodeDetails episode) { episodes.remove(episode); }
}
