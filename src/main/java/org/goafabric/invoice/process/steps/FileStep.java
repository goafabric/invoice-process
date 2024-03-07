package org.goafabric.invoice.process.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
