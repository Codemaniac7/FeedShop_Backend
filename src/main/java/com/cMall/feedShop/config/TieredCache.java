package com.cMall.feedShop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

/**
 * 이중 캐시 구현체
 * - L1 (Caffeine): 초고속 메모리 캐시
 * - L2 (Redis): 분산 캐시
 */
@Slf4j
public class TieredCache implements Cache {

    private final String name;
    private final Cache l1Cache; // Caffeine
    private final Cache l2Cache; // Redis (nullable)
    private final boolean redisEnabled;

    public TieredCache(String name, Cache l1Cache, @Nullable Cache l2Cache, boolean redisEnabled) {
        this.name = name;
        this.l1Cache = l1Cache;
        this.l2Cache = l2Cache;
        this.redisEnabled = redisEnabled;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return l1Cache.getNativeCache(); // 통계는 L1 캐시 기준
    }

    @Override
    @Nullable
    public ValueWrapper get(Object key) {
        // 1. L1 캐시에서 먼저 조회
        ValueWrapper l1Value = l1Cache.get(key);
        if (l1Value != null) {
            log.debug("L1 캐시 히트: {} - {}", name, key);
            return l1Value;
        }

        // 2. L1 미스 시 L2 캐시에서 조회
        if (redisEnabled && l2Cache != null) {
            ValueWrapper l2Value = l2Cache.get(key);
            if (l2Value != null) {
                log.debug("L2 캐시 히트: {} - {}", name, key);
                // L2에서 찾은 데이터를 L1에 저장 (캐시 워밍)
                l1Cache.put(key, l2Value.get());
                return l2Value;
            }
        }

        log.debug("캐시 미스: {} - {}", name, key);
        return null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper wrapper = get(key);
        return wrapper != null ? (T) wrapper.get() : null;
    }

    @Override
    @Nullable
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);
        if (wrapper != null) {
            return (T) wrapper.get();
        }

        // 캐시 미스 시 valueLoader로 데이터 로드
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new RuntimeException("캐시 값 로드 실패", e);
        }
    }

    @Override
    public void put(Object key, @Nullable Object value) {
        // L1, L2 모두에 저장
        l1Cache.put(key, value);

        if (redisEnabled && l2Cache != null) {
            try {
                l2Cache.put(key, value);
                log.debug("L1+L2 캐시 저장: {} - {}", name, key);
            } catch (Exception e) {
                log.warn("L2 캐시 저장 실패: {} - {}, 에러: {}", name, key, e.getMessage());
            }
        } else {
            log.debug("L1 캐시 저장: {} - {}", name, key);
        }
    }

    @Override
    @Nullable
    public ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
        ValueWrapper existingValue = get(key);
        if (existingValue == null) {
            put(key, value);
        }
        return existingValue;
    }

    @Override
    public void evict(Object key) {
        // L1, L2 모두에서 제거
        l1Cache.evict(key);

        if (redisEnabled && l2Cache != null) {
            try {
                l2Cache.evict(key);
                log.debug("L1+L2 캐시 제거: {} - {}", name, key);
            } catch (Exception e) {
                log.warn("L2 캐시 제거 실패: {} - {}, 에러: {}", name, key, e.getMessage());
            }
        } else {
            log.debug("L1 캐시 제거: {} - {}", name, key);
        }
    }

    @Override
    public void clear() {
        // L1, L2 모두 클리어
        l1Cache.clear();

        if (redisEnabled && l2Cache != null) {
            try {
                l2Cache.clear();
                log.debug("L1+L2 캐시 전체 클리어: {}", name);
            } catch (Exception e) {
                log.warn("L2 캐시 클리어 실패: {}, 에러: {}", name, e.getMessage());
            }
        } else {
            log.debug("L1 캐시 전체 클리어: {}", name);
        }
    }
}