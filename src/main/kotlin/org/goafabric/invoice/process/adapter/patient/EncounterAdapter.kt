package org.goafabric.invoice.process.adapter.patient

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.goafabric.invoice.process.adapter.patient.dto.Encounter
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@Retry(name = "encountersAdapter")
@CircuitBreaker(name = "encountersAdapter")
interface EncounterAdapter {
    @GetExchange("encounters/findByPatientIdAndDisplay")
    fun findByPatientIdAndDisplay(
        @RequestParam("patientId") patientId: String,
        @RequestParam("display") display: String
    ): List<Encounter>
}