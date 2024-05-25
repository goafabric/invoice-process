package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.adapter.authorization.LockAdapter;
import org.goafabric.invoice.adapter.authorization.PermissionAdapter;
import org.goafabric.invoice.adapter.authorization.dto.Lock;
import org.goafabric.invoice.adapter.authorization.dto.PermissionCategory;
import org.goafabric.invoice.adapter.authorization.dto.PermissionType;
import org.goafabric.invoice.extensions.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LockAdapter lockAdapter;

    private final PermissionAdapter permissionAdapter;

    public AuthorizationStep(LockAdapter lockAdapter, PermissionAdapter permissionAdapter) {
        this.lockAdapter = lockAdapter;
        this.permissionAdapter = permissionAdapter;
    }

    public Lock acquireLock() {
        log.info("acquire lock");
        var lock = lockAdapter.acquireLockByKey("invoice-" + TenantContext.getTenantId());
        if (lock.isLocked()) { throw new IllegalStateException("process is already locked"); } else { return lock; }
    }

    public void releaseLock(Lock lock) {
        if (lock != null) {
            log.info("release lock");
            lockAdapter.removeLockById(lock.id());
        }
    }

    public void checkAuthorization() {
        log.info("check authorization");
        if (!permissionAdapter.hasPermission(TenantContext.getUserName(), PermissionCategory.PROCESS, PermissionType.INVOICE)) {
            throw new IllegalStateException("User " + TenantContext.getUserName() + " is not allowed to execute process");
        }
    }


}
