package org.goafabric.invoice.process;

import org.goafabric.invoice.adapter.organization.LockAdapter;
import org.goafabric.invoice.adapter.organization.UserAdapter;
import org.goafabric.invoice.adapter.organization.dto.Lock;
import org.goafabric.invoice.adapter.organization.dto.PermissionCategory;
import org.goafabric.invoice.adapter.organization.dto.PermissionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisabledInAotMode
public class InvoiceProcessTest {
    @MockBean
    private LockAdapter lockAdapter;

    @MockBean
    private UserAdapter userAdapter;

    @Autowired
    private InvoiceProcess invoiceProcess;

    @Test
    public void run() {
        when(lockAdapter.acquireLockByKey(anyString()))
                .thenReturn(new Lock("0", false, "key", LocalDateTime.now(), "user1"));

        when(userAdapter.hasPermission(eq("user1"), eq(PermissionCategory.PROCESS), eq(PermissionType.INVOICE)))
                .thenReturn(true);

        assertThat(invoiceProcess.run()).isTrue();
    }


}