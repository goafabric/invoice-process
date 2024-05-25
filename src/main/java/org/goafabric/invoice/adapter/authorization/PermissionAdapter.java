package org.goafabric.invoice.adapter.authorization;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.goafabric.invoice.adapter.authorization.dto.PermissionCategory;
import org.goafabric.invoice.adapter.authorization.dto.PermissionType;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@Retry(name = "permissionAdapter")
@CircuitBreaker(name = "permissionAdapter")
@CacheConfig(cacheNames = "permissionAdapter")
public interface PermissionAdapter {
    @Cacheable
    @GetExchange("users/hasPermission") //we are using this rather costly method here, to avoid copying to much dto structures for users, roles etc
    Boolean hasPermission(@RequestParam("name") String name, @RequestParam("category") PermissionCategory category, @RequestParam("type") PermissionType type);
}