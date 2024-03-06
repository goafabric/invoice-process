package org.goafabric.invoice.process;

import org.goafabric.invoice.process.steps.AccessStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AccessStep accessStep;

    @Value("${test.mode:false}") Boolean testMode;

    public InvoiceProcess(AccessStep accessStep) {
        this.accessStep = accessStep;
    }

    @Override
    public void run(String... args) {
        if ( ((args.length > 0) && ("-check-integrity".equals(args[0]))) || (testMode) ){
            return;
        }

        run();
    }

    boolean run() {
        var lock = accessStep.acquireLock();
        try {
            accessStep.checkAuthorization();
            retrieveRecords();
            checkFile();
            encryptFile();
            sendFile();
            storeFile();
        } finally {
            accessStep.removeLock(lock);
        }
        return true;
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
