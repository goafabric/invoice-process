package org.goafabric.invoice.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess implements CommandLineRunner {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run(String... args) throws Exception {
        acquireLock();
        checkAuthorization();
        retrieveRecords();
        checkFile();
        encryptFile();
        sendFile();
        storeFile();
    }


    public void acquireLock() {
        log.info("acquire lock");
    }

    public void checkAuthorization() {
        log.info("check authorization");
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
