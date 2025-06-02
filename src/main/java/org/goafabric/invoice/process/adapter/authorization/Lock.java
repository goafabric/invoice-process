package org.goafabric.invoice.process.adapter.authorization;

import java.time.LocalDateTime;

public record Lock(String id, boolean isLocked, String lockKey, LocalDateTime lockTime, String userName) {}
