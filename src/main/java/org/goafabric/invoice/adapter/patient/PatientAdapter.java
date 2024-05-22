package org.goafabric.invoice.adapter.patient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.adapter.patient.dto.PatientNamesOnly;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@Retry(name = "patientAdapter")
@CircuitBreaker(name = "patientAdapter")
public interface PatientAdapter {
    @GetExchange("patients/findPatientNamesByFamilyName")
    List<PatientNamesOnly> findPatientNamesByFamilyName(String search);
}