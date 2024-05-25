package org.goafabric.invoice.process.adapter.authorization

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.goafabric.invoice.process.adapter.authorization.dto.PermissionCategory
import org.goafabric.invoice.process.adapter.authorization.dto.PermissionType
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

@Retry(name = "permissionAdapter")
@CircuitBreaker(name = "permissionAdapter")
@CacheConfig(cacheNames = ["permissionAdapter"])
interface PermissionAdapter {
    @Cacheable
    @GetExchange("users/hasPermission")
    fun hasPermission(
        @RequestParam("name") name: String?,
        @RequestParam("category") category: PermissionCategory?,
        @RequestParam("type") type: PermissionType?
    ): Boolean
}