package org.goafabric.invoice.process.adapter.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@SuppressWarnings("java:S7180")
@Retry(name = "conditionAdapter")
@CircuitBreaker(name = "conditionAdapter")
@CacheConfig(cacheNames = "conditionAdapter")
public interface ConditionAdapter {
    @Cacheable
    @GetExchange("conditions/findByCode")
    Condition findByCode(@RequestParam("code") String code);
}