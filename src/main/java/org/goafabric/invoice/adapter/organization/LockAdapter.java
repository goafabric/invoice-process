package org.goafabric.invoice.adapter.organization;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.goafabric.invoice.adapter.organization.dto.Lock;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;

@CircuitBreaker(name = "LockAdapter")
public interface LockAdapter {
    @GetExchange("locks/acquireLockByKey")
    Lock acquireLockByKey(@RequestParam String lockKey);

    @DeleteExchange("locks/removeLockById")
    void removeLockById(@RequestParam String lockId);
}