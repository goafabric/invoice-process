package org.goafabric.invoice.process.adapter

import org.goafabric.calleeservice.extensions.TenantContext
import org.goafabric.invoice.process.adapter.authorization.LockAdapter
import org.goafabric.invoice.process.adapter.authorization.PermissionAdapter
import org.goafabric.invoice.process.adapter.catalog.ChargeItemAdapter
import org.goafabric.invoice.process.adapter.catalog.ConditionAdapter
import org.goafabric.invoice.process.adapter.patient.EncounterAdapter
import org.goafabric.invoice.process.adapter.patient.PatientAdapter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class AdapterConfiguration {
    @Value("\${adapter.coreservice.user.name:}")
    private val userName: String? = null

    @Value("\${adapter.coreservice.user.password:}")
    private val password: String? = null

    @Bean
    fun lockAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.coreservice.url}") url: String, @Value("\${adapter.timeout}") timeout: Long
    ): LockAdapter {
        return createAdapter(LockAdapter::class.java, builder, url, timeout)
    }

    @Bean
    fun userAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.coreservice.url}") url: String, @Value("\${adapter.timeout}") timeout: Long
    ): PermissionAdapter {
        return createAdapter(PermissionAdapter::class.java, builder, url, timeout)
    }

    @Bean
    fun patientAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.coreservice.url}") url: String, @Value("\${adapter.timeout}") timeout: Long
    ): PatientAdapter {
        return createAdapter(PatientAdapter::class.java, builder, url, timeout)
    }

    @Bean
    fun encounterAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.coreservice.url}") url: String, @Value("\${adapter.timeout}") timeout: Long
    ): EncounterAdapter {
        return createAdapter(EncounterAdapter::class.java, builder, url, timeout)
    }

    @Bean
    fun chargeItemAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.catalogservice.url:}") url: String, @Value("\${adapter.timeout}") timeout: Long
    ): ChargeItemAdapter {
        return createAdapter(ChargeItemAdapter::class.java, builder, url, timeout)
    }

    @Bean
    fun conditionAdapter(
        builder: RestClient.Builder,
        @Value("\${adapter.catalogservice.url:}") url: String, @Value("\${adapter.timeout}") timeout: Long
    ): ConditionAdapter {
        return createAdapter(ConditionAdapter::class.java, builder, url, timeout)
    }

    fun <A> createAdapter(adapterType: Class<A>?, builder: RestClient.Builder, url: String, timeout: Long): A {
        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(timeout.toInt())
        requestFactory.setReadTimeout(timeout.toInt())
        builder.baseUrl(url)
            .requestInterceptor { request: HttpRequest, body: ByteArray?, execution: ClientHttpRequestExecution ->
                request.headers.setBasicAuth(userName, password)
                TenantContext.adapterHeaderMap
                    .forEach { (key: String, value: String) -> request.headers[key] = value }
                execution.execute(request, body)
            }
            .requestFactory(requestFactory)
        return HttpServiceProxyFactory.builderFor(RestClientAdapter.create(builder.build())).build()
            .createClient(adapterType)
    }
}


