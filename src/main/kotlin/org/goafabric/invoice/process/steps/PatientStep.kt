package org.goafabric.invoice.process.steps

import org.goafabric.invoice.process.adapter.catalog.ChargeItemAdapter
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter
import org.goafabric.invoice.process.adapter.patient.PatientAdapter
import org.goafabric.invoice.process.adapter.patient.dto.Encounter
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecord
import org.goafabric.invoice.process.adapter.patient.dto.MedicalRecordType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class PatientStep(
    private val patientAdapter: PatientAdapter,
    private val encounterAdapter: EncounterAdapter,
    private val chargeItemAdapter: ChargeItemAdapter,
    private val conditionAdapter: ConditionAdapter
) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${adapter.catalogservice.url:}")
    private val catalogServiceUrl: String? = null

    fun retrieveRecords(familyName: String?) {
        log.info("retrieve records")
        val patients = patientAdapter.findPatientNamesByFamilyName(familyName)
        if (!patients.isEmpty()) {
            val encounters = encounterAdapter.findByPatientIdAndDisplay(patients.first().id, "")
            logChargeItems(encounters)
            logConditions(encounters)
        }
    }

    private fun logChargeItems(encounters: List<Encounter>) {
        log.info("chargeitems")
        encounters.first().medicalRecords.stream()
            .filter { record: MedicalRecord -> record.type == MedicalRecordType.CHARGEITEM }
            .forEach { chargeItem: MedicalRecord ->
                log.info(chargeItem.toString())
                log.info(
                    if (!catalogServiceUrl!!.isEmpty()) chargeItemAdapter.findByCode(chargeItem.code).toString() else ""
                )
            }
    }

    private fun logConditions(encounters: List<Encounter>) {
        log.info("conditions")
        encounters.first().medicalRecords.stream()
            .filter { record: MedicalRecord -> record.type == MedicalRecordType.CONDITION }
            .filter { record: MedicalRecord -> record.code != "none" }
            .forEach { condition: MedicalRecord ->
                log.info(condition.toString())
                log.info(
                    if (!catalogServiceUrl!!.isEmpty()) conditionAdapter.findByCode(condition.code).toString() else ""
                )
            }
    }
}
