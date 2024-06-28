package org.goafabric.invoice.process.adapter.catalog;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.process.adapter.catalog.dto.ChargeItemEo;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Retry(name = "chargeItemAdapter")
@CircuitBreaker(name = "chargeItemAdapter")
@CacheConfig(cacheNames = "chargeItemAdapter")
//@RegisterReflectionForBinding(ChargeItemEo.class)
public interface ChargeItemAdapter {
    @Cacheable
    @GetExchange("chargeitems/findByCode")
    ChargeItemEo findByCode(@RequestParam("code") String code);
}