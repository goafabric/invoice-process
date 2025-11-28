package org.goafabric.invoice.process.adapter;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.goafabric.invoice.controller.extensions.UserContext;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@ImportRuntimeHints(MyCacheConfiguration.CacheRuntimeHints.class)
public class MyCacheConfiguration implements CachingConfigurer {

    private Long cacheMaxSize = 1000l;
    private Long cacheExpiry = 10l;

    @Bean
    @Profile("caffeine")
    @Override
    public CacheManager cacheManager() {
        final CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(cacheMaxSize).expireAfterAccess(cacheExpiry, TimeUnit.MINUTES));
        return cacheManager;
    }

    @Bean
    @Profile("redis")
    public org.springframework.data.redis.cache.RedisCacheConfiguration cacheConfiguration() {
        return org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(cacheExpiry))
                .disableCachingNullValues()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(GenericJacksonJsonRedisSerializer.builder().enableUnsafeDefaultTyping().build()));
    }

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) ->
                new SimpleKey(UserContext.getTenantId(), UserContext.getOrganizationId(), method.getName(), params);
    }

    static class CacheRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.reflection().registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMS", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.PSAMS", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerTypeIfPresent(classLoader, "com.github.benmanes.caffeine.cache.SSMSA", MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
            hints.reflection().registerType(TypeReference.of("com.github.benmanes.caffeine.cache.SSMSA"),
                    builder -> builder.withField("FACTORY"));
        }
    }

}
