package org.goafabric.invoice.process.adapter.patient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.process.adapter.patient.dto.PatientNamesOnly;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@Retry(name = "patientAdapter")
@CircuitBreaker(name = "patientAdapter")
@CacheConfig(cacheNames = "patientAdapter")
@RegisterReflectionForBinding(PatientNamesOnly.class)
public interface PatientAdapter {
    @Cacheable
    @GetExchange("patients/findPatientNamesByFamilyName")
    List<PatientNamesOnly> findPatientNamesByFamilyName(@RequestParam("search") String search);
}