package org.goafabric.invoice.process;

import org.goafabric.invoice.adapter.organization.LockAdapter;
import org.goafabric.invoice.adapter.organization.UserAdapter;
import org.goafabric.invoice.process.steps.LockStep;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.aot.DisabledInAotMode;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisabledInAotMode
class InvoiceProcessTest {
    @MockBean
    private LockAdapter lockAdapter;

    @MockBean
    private UserAdapter userAdapter;

    @MockBean
    private LockStep lockStep;

    @Autowired
    private InvoiceProcess invoiceProcess;

    @Test
    public void run() {
        //when(lockAdapter.acquireLockByKey(anyString())).thenReturn(new Lock("0", false, "key", LocalDateTime.now(), "user1"));
        assertThat(invoiceProcess.run()).isTrue();
    }


}