package org.goafabric.invoice.adapter.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.goafabric.invoice.adapter.catalog.dto.ChargeItemEo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;

@CircuitBreaker(name = "conditionAdapter")
@CacheConfig(cacheNames = "conditionAdapter")
public interface ConditionAdapter {
    @GetExchange("diagnosis/findByDisplay")
    List<ChargeItemEo> findByDisplay(@RequestParam("display") String display);
}