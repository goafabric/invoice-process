package org.goafabric.invoice.process.adapter.authorization.dto

import java.time.LocalDateTime

data class Lock(
    val id: String,
    val isLocked: Boolean,
    val lockKey: String,
    val lockTime: LocalDateTime,
    val userName: String
)
