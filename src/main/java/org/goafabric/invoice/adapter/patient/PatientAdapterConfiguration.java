package org.goafabric.invoice.adapter.patient;

import org.goafabric.invoice.extensions.HttpInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class PatientAdapterConfiguration {
    /*
    @Bean
    public LockAdapter lockAdapter(//ReactorLoadBalancerExchangeFilterFunction lbFunction,
                                   RestClient.Builder builder,
                                   @Value("${adapter.organizationservice.url}") String url, @Value("${adapter.timeout}") Long timeout, @Value("${adapter.maxlifetime:-1}") Long maxLifeTime) {
        return createAdapter(LockAdapter.class, builder, url, timeout, maxLifeTime);
    }

     */


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

}

