package org.goafabric.invoice.consumer.config;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Context;
import org.goafabric.invoice.controller.extensions.UserContext;
import org.slf4j.MDC;

public class ConsumerUtil {
    private ConsumerUtil() {
    }

    public static void withTenantInfos(Runnable runnable) {
        Span.fromContext(Context.current()).setAttribute("tenant.id", UserContext.getTenantId());
        MDC.put("tenantId", UserContext.getTenantId());
        try { runnable.run(); } finally { MDC.remove("tenantId"); }
    }
}
