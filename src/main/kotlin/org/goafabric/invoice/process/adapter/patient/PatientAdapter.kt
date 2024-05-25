package org.goafabric.invoice.process.adapter.patient

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.goafabric.invoice.process.adapter.patient.dto.PatientNamesOnly
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@Retry(name = "patientAdapter")
@CircuitBreaker(name = "patientAdapter")
interface PatientAdapter {
    @GetExchange("patients/findPatientNamesByFamilyName")
    fun findPatientNamesByFamilyName(@RequestParam("search") search: String): List<PatientNamesOnly>
}