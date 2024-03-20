package org.goafabric.invoice.process;

import org.goafabric.invoice.process.steps.AccessStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.PatientStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcess {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final AccessStep accessStep;
    private final InvoiceStep invoiceStep;
    private final PatientStep patientStep;


    public InvoiceProcess(AccessStep accessStep, InvoiceStep invoiceStep, PatientStep patientStep) {
        this.accessStep = accessStep;
        this.invoiceStep = invoiceStep;
        this.patientStep = patientStep;
    }

    boolean run() {
        accessStep.checkAuthorization();
        var lock = accessStep.acquireLock();
        try {
            patientStep.retrieveRecords();
                var invoice = invoiceStep.create();
                    invoiceStep.check(invoice);
                        var encryptedInvoice = invoiceStep.encrypt(invoice);
                            invoiceStep.send(encryptedInvoice);
                                invoiceStep.store(encryptedInvoice);
        }
        catch (Exception e) {
            log.error("error during process {}", e.getMessage());
        }
        finally {
            accessStep.releaseLock(lock);
        }
        return true;
    }

}
