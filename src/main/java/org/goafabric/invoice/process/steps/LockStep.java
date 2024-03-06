package org.goafabric.invoice.process.steps;

import org.goafabric.invoice.adapter.organization.LockAdapter;
import org.goafabric.invoice.adapter.organization.dto.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LockStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LockAdapter lockAdapter;

    public LockStep(LockAdapter lockAdapter) {
        this.lockAdapter = lockAdapter;
    }

    public Lock acquireLock() {
        log.info("acquire lock");
        var lock = lockAdapter.acquireLockByKey("invoice");
        if (lock.isLocked()) { throw new IllegalStateException("process is already locked"); }
        return lock;
    }

    public void removeLock(Lock lock) {
        log.info("remove lock");
        lockAdapter.removeLockById(lock.id());
    }

}
