package org.goafabric.invoice.process;

import org.goafabric.invoice.process.steps.AccessStep;
import org.goafabric.invoice.process.steps.FileStep;
import org.goafabric.invoice.process.steps.ReadStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${test.mode:false}") Boolean testMode;

    private final AccessStep accessStep;

    private final FileStep fileStep;

    private final ReadStep readStep;


    public InvoiceProcess(AccessStep accessStep, FileStep fileStep, ReadStep readStep) {
        this.accessStep = accessStep;
        this.fileStep = fileStep;
        this.readStep = readStep;
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
            readStep.retrieveRecords();
            fileStep.checkFile();
            fileStep.encryptFile();
            fileStep.sendFile();
            fileStep.storeFile();
        } finally {
            accessStep.removeLock(lock);
        }
        return true;
    }





}
