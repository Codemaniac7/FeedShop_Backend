package com.cMall.feedShop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
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

@Configuration
@EnableCaching
public class CacheConfig {

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

    /**
     * 이중 캐시 매니저 (L1: Caffeine + L2: Redis)
     */
    @Bean
    @Primary
    public CacheManager tieredCacheManager(RedisConnectionFactory redisConnectionFactory) {
        CacheManager l1CacheManager = createCaffeineCacheManager();
        CacheManager l2CacheManager = redisEnabled ? createRedisCacheManager(redisConnectionFactory) : null;
        
        return new TieredCacheManager(l1CacheManager, l2CacheManager, redisEnabled);
    }

    /**
     * Caffeine 캐시 매니저 (L1 캐시)
     * - 애플리케이션 레벨 캐시
     * - 매우 빠른 응답속도
     */
    private CacheManager createCaffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .maximumSize(caffeineMaximumSize)
                .expireAfterWrite(caffeineExpireAfterWriteMinutes, TimeUnit.MINUTES);
        
        if (caffeineRecordStats) {
            caffeineBuilder.recordStats();
        }
        
        cacheManager.setCaffeine(caffeineBuilder);
        
        // 캐시 이름 등록
        cacheManager.setCacheNames(java.util.List.of(
                "categories",           // 카테고리 목록
                "bestProducts",         // 베스트 상품 목록
                "popularProducts",      // 인기 상품 페이지
                "availableEvents"       // 이벤트 (기존)
        ));
        
        return cacheManager;
    }

    /**
     * Redis 캐시 매니저 (L2 캐시)
     * - 분산 캐시
     * - 여러 인스턴스 간 데이터 공유
     */
    private CacheManager createRedisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        if (!redisEnabled) {
            return null;
        }

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(redisTtlMinutes))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();
    }
} 