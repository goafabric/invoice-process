package org.goafabric.invoice.process.adapter

import com.github.benmanes.caffeine.cache.Caffeine
import org.goafabric.calleeservice.extensions.TenantContext
import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.caffeine.CaffeineCacheManager
import org.springframework.cache.interceptor.KeyGenerator
import org.springframework.cache.interceptor.SimpleKey
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.ImportRuntimeHints
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

//implementation("com.github.ben-manes.caffeine:caffeine"); implementation("org.springframework.boot:spring-boot-starter-cache");
@Configuration
@EnableCaching
@ImportRuntimeHints(CacheConfiguration.CacheRuntimeHints::class)
class CacheConfiguration : CachingConfigurer {
    private val cacheMaxSize = 1000L

    private val cacheExpiry = 10L

    @Bean
    override fun cacheManager(): CacheManager {
        val cacheManager = CaffeineCacheManager()
        cacheManager.setCaffeine(
            Caffeine.newBuilder()
                .maximumSize(cacheMaxSize)
                .expireAfterAccess(cacheExpiry, TimeUnit.MINUTES)
        )
        return cacheManager
    }

    @Bean
    override fun keyGenerator(): KeyGenerator {
        return KeyGenerator { target: Any?, method: Method, params: Array<Any?>? ->
            SimpleKey(
                TenantContext.tenantId,
                TenantContext.organizationId,
                method.name,
                params
            )
        }
    }

    internal class CacheRuntimeHints : RuntimeHintsRegistrar {
        override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader) {
            try { //caffeine hints
                hints.reflection().registerType(
                    Class.forName("com.github.benmanes.caffeine.cache.SSMSA"),
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
                )
                hints.reflection().registerType(
                    Class.forName("com.github.benmanes.caffeine.cache.PSAMS"),
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS
                )
            } catch (e: ClassNotFoundException) {
                throw RuntimeException(e)
            }
        }
    }
}
