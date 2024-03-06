package org.goafabric.invoice.adapter.organization;

import org.goafabric.invoice.extensions.HttpInterceptor;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@ImportRuntimeHints(OrganizationAdapterConfiguration.AdapterRuntimeHints.class)
public class OrganizationAdapterConfiguration {
    @Bean
    public LockAdapter lockAdapter(//ReactorLoadBalancerExchangeFilterFunction lbFunction,
                                                     RestClient.Builder builder,
            @Value("${adapter.organizationservice.url}") String url, @Value("${adapter.timeout}") Long timeout, @Value("${adapter.maxlifetime:-1}") Long maxLifeTime) {
        return createAdapter(LockAdapter.class, builder, url, timeout, maxLifeTime);
    }

    @Bean
    public UserAdapter userAdapter(//ReactorLoadBalancerExchangeFilterFunction lbFunction,
                                   RestClient.Builder builder,
                                   @Value("${adapter.organizationservice.url}") String url, @Value("${adapter.timeout}") Long timeout, @Value("${adapter.maxlifetime:-1}") Long maxLifeTime) {
        return createAdapter(UserAdapter.class, builder, url, timeout, maxLifeTime);
    }

    public static <A> A createAdapter(Class<A> adapterType, RestClient.Builder builder, String url, Long timeout, Long maxLifeTime) {
        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout.intValue());
        requestFactory.setReadTimeout(timeout.intValue());
        builder.baseUrl(url)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add("X-Access-Token", HttpInterceptor.getToken());
                    httpHeaders.add("X-TenantId", HttpInterceptor.getTenantId());
                    httpHeaders.add("X-OrganizationId", HttpInterceptor.getOrganizationId());
                })
                .requestFactory(requestFactory);
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
                .createClient(adapterType);
    }

    static class AdapterRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerType(io.github.resilience4j.spring6.circuitbreaker.configure.CircuitBreakerAspect.class,
                    builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_METHODS));
        }
    }

}

