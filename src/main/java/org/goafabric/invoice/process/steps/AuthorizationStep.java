package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.controller.extensions.TenantContext;
import org.goafabric.invoice.process.adapter.authorization.Lock;
import org.goafabric.invoice.process.adapter.authorization.LockAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LockAdapter lockAdapter;


    public AuthorizationStep(LockAdapter lockAdapter) {
        this.lockAdapter = lockAdapter;
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

}
