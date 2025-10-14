package com.cMall.feedShop.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.cache.caffeine.enabled:true}")
    private boolean caffeineEnabled;

    @Value("${spring.cache.redis.enabled:true}")
    private boolean redisEnabled;

    @Value("${spring.cache.caffeine.maximum-size:1000}")
    private long caffeineMaximumSize;

    @Value("${spring.cache.caffeine.expire-after-write:30}")
    private long caffeineExpireAfterWriteMinutes;

    @Value("${spring.cache.caffeine.record-stats:true}")
    private boolean caffeineRecordStats;

    @Value("${spring.cache.redis.time-to-live:60}")
    private long redisTtlMinutes;

    @Bean
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        if (caffeineEnabled && redisEnabled) {
            log.info("Initializing Tiered Cache (L1: Caffeine, L2: Redis)");
            CacheManager l1CacheManager = createCaffeineCacheManager();
            CacheManager l2CacheManager = createRedisCacheManager(redisConnectionFactory);
            return new TieredCacheManager(l1CacheManager, l2CacheManager, true);
        } else if (caffeineEnabled) {
            log.info("Initializing L1 Cache only (Caffeine)");
            return createCaffeineCacheManager();
        } else if (redisEnabled) {
            log.info("Initializing L2 Cache only (Redis)");
            return createRedisCacheManager(redisConnectionFactory);
        } else {
            log.info("All caching is disabled.");
            return new NoOpCacheManager();
        }
    }

    private CaffeineCacheManager createCaffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .maximumSize(caffeineMaximumSize)
                .expireAfterWrite(caffeineExpireAfterWriteMinutes, TimeUnit.MINUTES);
        if (caffeineRecordStats) {
            caffeineBuilder.recordStats();
        }
        cacheManager.setCaffeine(caffeineBuilder);
        cacheManager.setCacheNames(java.util.List.of("categories", "bestProducts", "popularProducts", "availableEvents"));
        return cacheManager;
    }

    private RedisCacheManager createRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        System.out.println("\n!!!!!!!!!! 1. ENTERING createRedisCacheManager !!!!!!!!!!\n");

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );

        System.out.println("\n!!!!!!!!!! 2. ObjectMapper CONFIGURED FOR PROPERTY-BASED TYPING !!!!!!!!!!\n");

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(redisTtlMinutes))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        System.out.println("\n!!!!!!!!!! 3. RedisCacheConfiguration CREATED !!!!!!!!!!\n");

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
}