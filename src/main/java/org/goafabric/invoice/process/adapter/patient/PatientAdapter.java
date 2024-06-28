package org.goafabric.invoice.process.adapter.patient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.process.adapter.patient.dto.Patient;
import org.goafabric.invoice.process.adapter.patient.dto.PatientNamesOnly;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;

@Retry(name = "patientAdapter")
@CircuitBreaker(name = "patientAdapter")
public interface PatientAdapter {
    @GetExchange("/patients/findPatientNamesByFamilyName")
    List<PatientNamesOnly> findPatientNamesByFamilyName(@RequestParam("search") String search);

    @GetExchange("/patients/getById/{id}")
    Patient getById(@PathVariable("id") String id);

    @PostExchange("/patients/save")
    Patient save(@RequestBody Patient patient);
}