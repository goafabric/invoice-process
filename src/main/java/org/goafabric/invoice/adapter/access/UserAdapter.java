package org.goafabric.invoice.adapter.access;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.goafabric.invoice.adapter.access.dto.PermissionCategory;
import org.goafabric.invoice.adapter.access.dto.PermissionType;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@CircuitBreaker(name = "UserAdapter")
@CacheConfig(cacheNames = "UserAdapter")
public interface UserAdapter {
    @Cacheable
    @GetExchange("users/hasPermission") //we are using this rather costly method here, to avoid copying to much dto structures for users, roles etc
    Boolean hasPermission(@RequestParam("name") String name, @RequestParam("category") PermissionCategory category, @RequestParam("type") PermissionType type);
}