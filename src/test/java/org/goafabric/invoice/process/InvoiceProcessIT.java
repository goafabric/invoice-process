package org.goafabric.invoice.process;

import org.goafabric.invoice.process.adapter.authorization.LockAdapter;
import org.goafabric.invoice.process.adapter.authorization.PermissionAdapter;
import org.goafabric.invoice.process.adapter.authorization.dto.Lock;
import org.goafabric.invoice.process.adapter.authorization.dto.PermissionCategory;
import org.goafabric.invoice.process.adapter.authorization.dto.PermissionType;
import org.goafabric.invoice.process.adapter.catalog.ChargeItemAdapter;
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.process.adapter.patient.PatientAdapter;
import org.goafabric.invoice.process.adapter.s3.S3Adapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisabledInAotMode
public class InvoiceProcessIT {
    @MockBean
    private LockAdapter lockAdapter;

    @MockBean
    private PermissionAdapter permissionAdapter;

    @MockBean
    private PatientAdapter patientAdapter;

    @MockBean
    private EncounterAdapter encounterAdapter;

    @MockBean
    private ChargeItemAdapter chargeItemAdapter;

    @MockBean
    private ConditionAdapter conditionAdapter;

    @MockBean
    private S3Adapter s3Adapter;

    @Autowired
    private InvoiceProcess invoiceProcess;

    @Test
    public void run() throws Exception {
        when(permissionAdapter.hasPermission(anyString(), eq(PermissionCategory.PROCESS), eq(PermissionType.INVOICE)))
                .thenReturn(true);

        when(lockAdapter.acquireLockByKey("invoice-0"))
                .thenReturn(new Lock("0", false, "key", LocalDateTime.now(), "user"));

        assertThat(invoiceProcess.run().get()).isTrue();
    }

    @Test
    public void alreadyLocked() throws Exception{
        when(permissionAdapter.hasPermission(anyString(), eq(PermissionCategory.PROCESS), eq(PermissionType.INVOICE)))
                .thenReturn(true);

        when(lockAdapter.acquireLockByKey("invoice-0"))
                .thenReturn(new Lock("0", true, "key", LocalDateTime.now(), "user"));

        assertThatThrownBy(() -> invoiceProcess.run().get()).cause().isInstanceOf(IllegalStateException.class);
    }

    /*
    @Test
    public void notPermitted() {
        when(userAdapter.hasPermission(anyString(), eq(PermissionCategory.PROCESS), eq(PermissionType.INVOICE)))
                .thenReturn(false);

        when(lockAdapter.acquireLockByKey("invoice"))
                .thenReturn(new Lock("0", false, "key", LocalDateTime.now(), "user"));

        assertThatThrownBy(() -> invoiceProcess.run().get()).isInstanceOf(SecurityException.class);
    }
    */

}