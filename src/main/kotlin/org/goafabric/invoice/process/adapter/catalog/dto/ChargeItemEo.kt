package org.goafabric.invoice.process.adapter.catalog.dto

data class ChargeItemEo(
    val id: String,
    val version: Long,
    val code: String,
    val display: String,
    val price: Double
)
