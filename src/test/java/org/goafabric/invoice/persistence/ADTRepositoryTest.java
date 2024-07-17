package org.goafabric.invoice.persistence;

import org.goafabric.invoice.persistence.entity.ADTEntry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class ADTRepositoryTest {
    @Autowired
    private ADTRepository adtRepository = new ADTRepository();

    @Test
    void test() {
        var entry = new ADTEntry("PID", "1", "PID|Doe John|");
        adtRepository.save(entry);
        assertThat(adtRepository.findAll()).hasSize(1);
        assertThat(adtRepository.findAll().getFirst().entry()).contains("Doe John");

        adtRepository.delete(entry);
        assertThat(adtRepository.findAll()).isEmpty();

        adtRepository.save(new ADTEntry(entry.id(), entry.entryType(), entry.entryId(), "updated"));
        assertThat(adtRepository.findAll().getFirst().entry()).contains("updated");
    }

}