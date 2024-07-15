package org.goafabric.invoice.consumer;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.goafabric.invoice.controller.extensions.TenantContext;
import org.slf4j.MDC;

public class ConsumerUtil {
    public static void withTenantInfos(Runnable runnable) {
        Span.fromContext(Context.current()).setAttribute("tenant.id", TenantContext.getTenantId());
        MDC.put("tenantId", TenantContext.getTenantId());
        try { runnable.run(); } finally { MDC.remove("tenantId"); }
    }
}
