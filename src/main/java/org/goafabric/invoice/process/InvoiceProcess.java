package org.goafabric.invoice.process;

import org.goafabric.invoice.adapter.organization.UserAdapter;
import org.goafabric.invoice.adapter.organization.dto.PermissionCategory;
import org.goafabric.invoice.adapter.organization.dto.PermissionType;
import org.goafabric.invoice.extensions.HttpInterceptor;
import org.goafabric.invoice.process.steps.LockStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final LockStep lockStep;

    private final UserAdapter userAdapter;

    @Value("${test.mode:false}") Boolean testMode;

    public InvoiceProcess(LockStep lockStep, UserAdapter userAdapter) {
        this.lockStep = lockStep;
        this.userAdapter = userAdapter;
    }

    @Override
    public void run(String... args) {
        if ( ((args.length > 0) && ("-check-integrity".equals(args[0]))) || (testMode) ){
            return;
        }

        run();
    }

    boolean run() {
        var lock = lockStep.acquireLock();
        try {
            checkAuthorization();
            retrieveRecords();
            checkFile();
            encryptFile();
            sendFile();
            storeFile();
        } finally {
            lockStep.removeLock(lock);
        }
        return true;
    }

    public void checkAuthorization() {
        log.info("check authorization");
        if (!userAdapter.hasPermission(HttpInterceptor.getUserName(), PermissionCategory.PROCESS, PermissionType.INVOICE)) {
            throw new IllegalStateException("User is not allowed to execute process");
        }
        if (!userAdapter.hasPermission(HttpInterceptor.getUserName(), PermissionCategory.PROCESS, PermissionType.INVOICE)) {
            throw new IllegalStateException("User is not allowed to execute process");
        }
    }

    public void retrieveRecords() {
        log.info("retrieve records");
    }

    public void checkFile() {
        log.info("checking file");
    }

    public void encryptFile() {
        log.info("encryption file");
    }

    public void sendFile() {
        log.info("sending file");
    }

    public void storeFile() {
        log.info("storing file");
    }

}
