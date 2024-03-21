package org.goafabric.invoice.extensions;

import jakarta.annotation.security.RolesAllowed;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.goafabric.invoice.adapter.access.UserAdapter;
import org.goafabric.invoice.adapter.access.dto.PermissionCategory;
import org.goafabric.invoice.adapter.access.dto.PermissionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Component;

@Component
@Aspect
@ImportRuntimeHints(AuthorizationChecker.ApplicationRuntimeHints.class)
public class AuthorizationChecker {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserAdapter userAdapter;

    @Around("@annotation(rolesAllowed)")
    public Object checkRoles(ProceedingJoinPoint joinPoint, RolesAllowed rolesAllowed) throws Throwable {
        var permission = rolesAllowed.value()[0];
        if (!userAdapter.hasPermission(TenantContext.getUserName(), PermissionCategory.PROCESS, PermissionType.valueOf(permission))) {
            throw new SecurityException("Access denied. User does not have required permissions.");
        }
        return joinPoint.proceed();
    }

    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(AuthorizationChecker.class, MemberCategory.INVOKE_DECLARED_METHODS);
        }
    }

}
