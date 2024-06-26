package org.goafabric.invoice.process.adapter.authorization.dto;

import java.time.LocalDateTime;

public record Lock(String id, Boolean isLocked, String lockKey, LocalDateTime lockTime, String userName) {}
