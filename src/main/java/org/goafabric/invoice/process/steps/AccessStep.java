package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.adapter.access.LockAdapter;
import org.goafabric.invoice.adapter.access.UserAdapter;
import org.goafabric.invoice.adapter.access.dto.Lock;
import org.goafabric.invoice.adapter.access.dto.PermissionCategory;
import org.goafabric.invoice.adapter.access.dto.PermissionType;
import org.goafabric.invoice.extensions.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AccessStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LockAdapter lockAdapter;

    private final UserAdapter userAdapter;

    public AccessStep(LockAdapter lockAdapter, UserAdapter userAdapter) {
        this.lockAdapter = lockAdapter;
        this.userAdapter = userAdapter;
    }

    public Lock acquireLock() {
        log.info("acquire lock");
        var lock = lockAdapter.acquireLockByKey("invoice" + TenantContext.getTenantId());
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
        if (!userAdapter.hasPermission(TenantContext.getUserName(), PermissionCategory.PROCESS, PermissionType.INVOICE)) {
            throw new IllegalStateException("User " + TenantContext.getUserName() + " is not allowed to execute process");
        }
    }


}
