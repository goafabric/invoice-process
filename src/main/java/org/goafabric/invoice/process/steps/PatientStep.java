package org.goafabric.invoice.process.steps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PatientStep {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public void retrieveRecords() {
        log.info("retrieve records");
    }

}
