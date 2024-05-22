package org.goafabric.invoice.process;

import org.goafabric.invoice.adapter.access.LockAdapter;
import org.goafabric.invoice.adapter.access.UserAdapter;
import org.goafabric.invoice.adapter.access.dto.Lock;
import org.goafabric.invoice.adapter.access.dto.PermissionCategory;
import org.goafabric.invoice.adapter.access.dto.PermissionType;
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
    private UserAdapter userAdapter;

    @Autowired
    private InvoiceProcess invoiceProcess;

    @Test
    public void run() throws Exception {
        when(userAdapter.hasPermission(anyString(), eq(PermissionCategory.PROCESS), eq(PermissionType.INVOICE)))
                .thenReturn(true);

        when(lockAdapter.acquireLockByKey("invoice"))
                .thenReturn(new Lock("0", false, "key", LocalDateTime.now(), "user"));

        assertThat(invoiceProcess.run().get()).isTrue();
    }

    @Test
    public void alreadyLocked() throws Exception{
        when(userAdapter.hasPermission(anyString(), eq(PermissionCategory.PROCESS), eq(PermissionType.INVOICE)))
                .thenReturn(true);

        when(lockAdapter.acquireLockByKey("invoice"))
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