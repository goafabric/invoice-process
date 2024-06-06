//package org.goafabric.invoice.process.adapter;
//
//import org.goafabric.invoice.controller.extensions.TenantContext;
//import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
//import org.springframework.cache.annotation.CachingConfigurer;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.interceptor.KeyGenerator;
//import org.springframework.cache.interceptor.SimpleKey;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.Profile;
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializationContext;
//
//import java.time.Duration;
//
//@Configuration
//@EnableCaching
//@Profile("redis")
//@Import(RedisAutoConfiguration.class)
//public class RedisCacheConfiguration implements CachingConfigurer {
//
//    private Long cacheMaxSize = 1000l;
//    private Long cacheExpiry = 10l;
//
//    @Bean
//    public org.springframework.data.redis.cache.RedisCacheConfiguration cacheConfiguration() {
//        return org.springframework.data.redis.cache.RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(cacheExpiry))
//                .disableCachingNullValues()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//    }
//
//    @Bean
//    public KeyGenerator keyGenerator() {
//        return (target, method, params) ->
//                new SimpleKey(TenantContext.getTenantId(), TenantContext.getOrganizationId(), method.getName(), params);
//    }
//
//}
