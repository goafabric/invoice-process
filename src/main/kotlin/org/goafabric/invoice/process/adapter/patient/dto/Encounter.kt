package org.goafabric.invoice.process.adapter.patient.dto

import java.time.LocalDate

data class Encounter(
    val id: String,
    val version: Long,
    val patientId: String,
    val practitionerId: String,
    val encounterDate: LocalDate,
    val encounterName: String,
    val medicalRecords: List<MedicalRecord>
)
