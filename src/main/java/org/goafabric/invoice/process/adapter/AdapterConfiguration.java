package org.goafabric.invoice.process.adapter;

import org.goafabric.invoice.process.adapter.authorization.LockAdapter;
import org.goafabric.invoice.process.adapter.authorization.PermissionAdapter;
import org.goafabric.invoice.process.adapter.catalog.ChargeItemAdapter;
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter;
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter;
import org.goafabric.invoice.process.adapter.patient.PatientAdapter;
import org.goafabric.invoice.controller.extensions.TenantContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AdapterConfiguration {
    private @Value("${adapter.coreservice.user.name:}") String userName;
    private @Value("${adapter.coreservice.user.password:}") String password;

    @Bean
    public LockAdapter lockAdapter(RestClient.Builder builder,
                                   @Value("${adapter.coreservice.url}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(LockAdapter.class, builder, url, timeout);
    }

    @Bean
    public PermissionAdapter userAdapter(RestClient.Builder builder,
                                         @Value("${adapter.coreservice.url}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(PermissionAdapter.class, builder, url, timeout);
    }

    @Bean
    public PatientAdapter patientAdapter(RestClient.Builder builder,
                                         @Value("${adapter.coreservice.url}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(PatientAdapter.class, builder, url, timeout);
    }

    @Bean
    public EncounterAdapter encounterAdapter(RestClient.Builder builder,
                                             @Value("${adapter.coreservice.url}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(EncounterAdapter.class, builder, url, timeout);
    }

    @Bean
    public ChargeItemAdapter chargeItemAdapter(RestClient.Builder builder,
                                               @Value("${adapter.catalogservice.url:}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(ChargeItemAdapter.class, builder, url, timeout);
    }

    @Bean
    public ConditionAdapter conditionAdapter(RestClient.Builder builder,
                                             @Value("${adapter.catalogservice.url:}") String url, @Value("${adapter.timeout}") Long timeout) {
        return createAdapter(ConditionAdapter.class, builder, url, timeout);
    }

    public <A> A createAdapter(Class<A> adapterType, RestClient.Builder builder, String url, Long timeout) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout.intValue());
        requestFactory.setReadTimeout(timeout.intValue());
        builder.baseUrl(url)
                .requestInterceptor((request, body, execution) -> {
                    request.getHeaders().setBasicAuth(userName, password);
                    TenantContext.getAdapterHeaderMap().forEach((key, value) -> request.getHeaders().set(key, value));
                    return execution.execute(request, body);
                })
                .requestFactory(requestFactory);
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
                .createClient(adapterType);
    }

}


