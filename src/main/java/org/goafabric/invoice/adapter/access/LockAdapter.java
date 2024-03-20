package org.goafabric.invoice.adapter.access;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.adapter.access.dto.Lock;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;

@Retry(name = "lockAdapter")
@CircuitBreaker(name = "lockAdapter")
public interface LockAdapter {
    @GetExchange("locks/acquireLockByKey")
    Lock acquireLockByKey(@RequestParam String lockKey);

    @DeleteExchange("locks/removeLockById")
    void removeLockById(@RequestParam String lockId);
}