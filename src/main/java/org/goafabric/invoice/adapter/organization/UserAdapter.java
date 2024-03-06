package org.goafabric.invoice.adapter.organization;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.goafabric.invoice.adapter.organization.dto.PermissionCategory;
import org.goafabric.invoice.adapter.organization.dto.PermissionType;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

@CircuitBreaker(name = "UserAdapter")
public interface UserAdapter {
    @GetExchange("users/hasPermission") //we are using this rather costly method here, to avoid copying to much dto structures for users, roles etc
    Boolean hasPermission(@RequestParam("name") String name, @RequestParam("category") PermissionCategory category, @RequestParam("type") PermissionType type);
}