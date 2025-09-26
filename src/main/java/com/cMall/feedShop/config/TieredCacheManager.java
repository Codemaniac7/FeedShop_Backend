package com.cMall.feedShop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 이중 캐시 매니저 (L1: Caffeine, L2: Redis)
 * - L1 캐시에서 먼저 조회
 * - L1 미스 시 L2에서 조회 후 L1에 저장
 * - 데이터 저장 시 L1, L2 모두 저장
 */
@Slf4j
public class TieredCacheManager implements CacheManager {
    
    private final CacheManager l1CacheManager; // Caffeine
    private final CacheManager l2CacheManager; // Redis
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();
    private final boolean redisEnabled;

    public TieredCacheManager(CacheManager l1CacheManager, @Nullable CacheManager l2CacheManager, boolean redisEnabled) {
        this.l1CacheManager = l1CacheManager;
        this.l2CacheManager = l2CacheManager;
        this.redisEnabled = redisEnabled && l2CacheManager != null;
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, cacheName -> {
            Cache l1Cache = l1CacheManager.getCache(cacheName);
            Cache l2Cache = redisEnabled ? l2CacheManager.getCache(cacheName) : null;
            
            if (l1Cache == null) {
                log.warn("L1 캐시 '{}' 를 찾을 수 없습니다.", cacheName);
                return null;
            }
            
            return new TieredCache(cacheName, l1Cache, l2Cache, redisEnabled);
        });
    }

    @Override
    public Collection<String> getCacheNames() {
        return l1CacheManager.getCacheNames();
    }
}