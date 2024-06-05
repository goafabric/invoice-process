package org.goafabric.invoice.process.adapter.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.process.adapter.catalog.dto.ConditionEo;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Retry(name = "conditionAdapter")
@CircuitBreaker(name = "conditionAdapter")
@CacheConfig(cacheNames = "conditionAdapter")
@RegisterReflectionForBinding(ConditionEo.class)
public interface ConditionAdapter {
    @Cacheable
    @GetExchange("conditions/findByCode")
    ConditionEo findByCode(@RequestParam("code") String code);
}