package org.goafabric.invoice.process.adapter.patient.dto

data class MedicalRecord(
    val id: String?,
    val encounterId: String?,
    val version: Long?,
    val type: MedicalRecordType?,
    val display: String?,
    val code: String?,
    val specialization: String?
)