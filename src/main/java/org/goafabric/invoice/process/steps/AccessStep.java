package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.adapter.organization.LockAdapter;
import org.goafabric.invoice.adapter.organization.UserAdapter;
import org.goafabric.invoice.adapter.organization.dto.Lock;
import org.goafabric.invoice.adapter.organization.dto.PermissionCategory;
import org.goafabric.invoice.adapter.organization.dto.PermissionType;
import org.goafabric.invoice.extensions.HttpInterceptor;
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
        var lock = lockAdapter.acquireLockByKey("invoice");
        if (lock.isLocked()) { throw new IllegalStateException("process is already locked"); } else { return lock; }
    }

    public void removeLock(Lock lock) {
        log.info("remove lock");
        lockAdapter.removeLockById(lock.id());
    }

    public void checkAuthorization() {
        log.info("check authorization");
        if (!userAdapter.hasPermission(HttpInterceptor.getUserName(), PermissionCategory.PROCESS, PermissionType.INVOICE)) {
            throw new IllegalStateException("User is not allowed to execute process");
        }
    }


}
