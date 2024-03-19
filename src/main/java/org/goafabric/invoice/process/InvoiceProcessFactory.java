package org.goafabric.invoice.process;

import org.goafabric.invoice.adapter.access.dto.Lock;
import org.goafabric.invoice.adapter.invoice.dto.Invoice;
import org.goafabric.invoice.process.steps.AccessStep;
import org.goafabric.invoice.process.steps.InvoiceStep;
import org.goafabric.invoice.process.steps.PatientStep;
import org.springframework.stereotype.Component;

@Component
public class InvoiceProcessFactory {

    private final AccessStep accessStep;

    private final InvoiceStep invoiceStep;

    private final PatientStep patientStep;
    
    public InvoiceProcessFactory (AccessStep accessStep, InvoiceStep invoiceStep, PatientStep patientStep) {
        this.accessStep = accessStep;
        this.invoiceStep = invoiceStep;
        this.patientStep = patientStep;
    }
    
    public InvoiceProcess create() {
        return new InvoiceProcess();    
    }

    class InvoiceProcess {
        Lock lock = null;
        Invoice invoice = null;

        InvoiceProcess checkAuthorization() {
            accessStep.checkAuthorization();
            return this;
        }

        InvoiceProcess acquireLock() {
            lock = accessStep.acquireLock();
            return this;
        }

        InvoiceProcess retrieveRecords() {
            patientStep.retrieveRecords();
            return this;
        }

        InvoiceProcess createInvoice() {
            invoice = invoiceStep.create();
            return this;
        }

        InvoiceProcess checkInvoice() {
            invoiceStep.check(invoice);
            return this;
        }

        InvoiceProcess encryptInvoice() {
            invoice = invoiceStep.encrypt(invoice);
            return this;
        }

        InvoiceProcess sendInvoice() {
            invoiceStep.send(invoice);
            return this;
        }

        InvoiceProcess storeInvoice() {
            invoiceStep.store(invoice);
            return this;
        }

        void releaseLock() {
            if (lock != null) {
                accessStep.releaseLock(lock);
            }
        }
    }
}
