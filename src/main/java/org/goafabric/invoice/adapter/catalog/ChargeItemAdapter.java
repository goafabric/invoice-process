package org.goafabric.invoice.adapter.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.adapter.catalog.dto.ChargeItemEo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Retry(name = "chargeItemAdapter")
@CircuitBreaker(name = "chargeItemAdapter")
@CacheConfig(cacheNames = "chargeItemAdapter")
public interface ChargeItemAdapter {
    @GetExchange("chargeitems/findByCode")
    ChargeItemEo findByCode(@RequestParam("code") String code);
}