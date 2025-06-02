package org.goafabric.invoice.process;

import org.goafabric.invoice.process.adapter.authorization.Lock;
import org.goafabric.invoice.process.adapter.authorization.LockAdapter;
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.process.adapter.patient.PatientAdapter;
import org.goafabric.invoice.process.adapter.s3.S3Adapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisabledInAotMode
class InvoiceProcessIT {
    @MockitoBean
    private LockAdapter lockAdapter;

    @MockitoBean
    private PatientAdapter patientAdapter;

    @MockitoBean
    private EncounterAdapter encounterAdapter;

    @MockitoBean
    private ConditionAdapter conditionAdapter;

    @MockitoBean
    private S3Adapter s3Adapter;

    @Autowired
    private InvoiceProcess invoiceProcess;

    @Test
    void run() throws Exception {

        when(lockAdapter.acquireLockByKey("invoice-0"))
                .thenReturn(new Lock("0", false, "key", LocalDateTime.now(), "user"));

        assertThat(invoiceProcess.run().get()).isTrue();
    }

    @Test
    void alreadyLocked() {
        when(lockAdapter.acquireLockByKey("invoice-0"))
                .thenReturn(new Lock("0", true, "key", LocalDateTime.now(), "user"));

        assertThatThrownBy(() -> invoiceProcess.run().get()).cause().isInstanceOf(IllegalStateException.class);
    }


}