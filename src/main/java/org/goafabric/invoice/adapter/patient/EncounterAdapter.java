package org.goafabric.invoice.adapter.patient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.adapter.patient.dto.Encounter;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@Retry(name = "encountersAdapter")
@CircuitBreaker(name = "encountersAdapter")
public interface EncounterAdapter {
    @GetExchange("encounters/findByPatientIdAndDisplay")
    List<Encounter> findByPatientIdAndDisplay(String patientId, String display);
}