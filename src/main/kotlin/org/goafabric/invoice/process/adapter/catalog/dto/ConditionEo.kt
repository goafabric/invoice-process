package org.goafabric.invoice.process.adapter.catalog.dto

@JvmRecord
data class ConditionEo(
    val id: String,
    val version: Long,
    val code: String,
    val display: String,
    val shortname: String
) 