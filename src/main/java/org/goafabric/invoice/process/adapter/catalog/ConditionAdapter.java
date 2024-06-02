package org.goafabric.invoice.process.adapter.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.process.adapter.catalog.dto.ConditionEo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Retry(name = "conditionAdapter")
@CircuitBreaker(name = "conditionAdapter")
@CacheConfig(cacheNames = "conditionAdapter")
public interface ConditionAdapter {
    @GetExchange("conditions/findByCode")
    ConditionEo findByCode(@RequestParam("code") String code);
}