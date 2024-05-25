package org.goafabric.invoice.process.steps

import org.goafabric.invoice.controller.extensions.TenantContext
import org.goafabric.invoice.process.adapter.authorization.LockAdapter
import org.goafabric.invoice.process.adapter.authorization.PermissionAdapter
import org.goafabric.invoice.process.adapter.authorization.dto.Lock
import org.goafabric.invoice.process.adapter.authorization.dto.PermissionCategory
import org.goafabric.invoice.process.adapter.authorization.dto.PermissionType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuthorizationStep(private val lockAdapter: LockAdapter, private val permissionAdapter: PermissionAdapter) {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun acquireLock(): Lock {
        log.info("acquire lock")
        val lock = lockAdapter.acquireLockByKey("invoice-" + TenantContext.getTenantId())
        check(!lock.isLocked) { "process is already locked" }
        return lock
    }

    fun releaseLock(lock: Lock?) {
        if (lock != null) {
            log.info("release lock")
            lockAdapter.removeLockById(lock.id)
        }
    }

    fun checkAuthorization() {
        log.info("check authorization")
        check(
            permissionAdapter.hasPermission(
                TenantContext.getUserName(),
                PermissionCategory.PROCESS,
                PermissionType.INVOICE
            )
        ) { "User " + TenantContext.getUserName() + " is not allowed to execute process" }
    }
}
