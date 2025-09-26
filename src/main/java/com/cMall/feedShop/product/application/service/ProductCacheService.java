package com.cMall.feedShop.product.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 상품 관련 캐시 관리 통합 서비스
 * - 수동 캐시 무효화
 * - AOP 기반 자동 캐시 무효화
 * - 캐시 통계 및 모니터링
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {
    private final CacheManager cacheManager;

    // ========== AOP 기반 자동 캐시 무효화 ==========

    /**
     * 상품 생성 시 캐시 무효화
     * - 새로운 상품이 추가되면 베스트 상품 목록이 변경될 수 있음
     */
    @CacheEvict(value = { "bestProducts", "popularProducts" }, allEntries = true)
    public void evictOnProductCreate() {
        log.info("상품 생성으로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 수정 시 캐시 무효화
     * - 상품 정보 변경으로 인해 베스트 상품 순위가 변경될 수 있음
     */
    @CacheEvict(value = { "bestProducts", "popularProducts" }, allEntries = true)
    public void evictOnProductUpdate() {
        log.info("상품 수정으로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 삭제 시 캐시 무효화
     * - 상품이 삭제되면 베스트 상품 목록에서 제외되어야 함
     */
    @CacheEvict(value = { "bestProducts", "popularProducts" }, allEntries = true)
    public void evictOnProductDelete() {
        log.info("상품 삭제로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 재고 변경 시 캐시 무효화
     * - 재고 상태 변경으로 인해 재고 필터링된 베스트 상품 목록이 변경될 수 있음
     */
    @CacheEvict(value = { "bestProducts", "popularProducts" }, allEntries = true)
    public void evictOnStockChange() {
        log.info("상품 재고 변경으로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 인기도 변경 시 캐시 무효화 (위시리스트, 주문 등)
     * - 인기도 변경으로 인해 베스트 상품 순위가 변경될 수 있음
     */
    @CacheEvict(value = { "bestProducts", "popularProducts" }, allEntries = true)
    public void evictOnPopularityChange() {
        log.info("상품 인기도 변경으로 인한 캐시 무효화 실행");
    }

    /**
     * 카테고리 변경 시 캐시 무효화
     * - 카테고리 정보 변경으로 인해 카테고리 목록과 카테고리별 베스트 상품이 변경될 수 있음
     */
    @CacheEvict(value = { "categories", "bestProducts" }, allEntries = true)
    public void evictOnCategoryChange() {
        log.info("카테고리 변경으로 인한 캐시 무효화 실행");
    }

    // ========== 수동 캐시 관리 (관리자용) ==========

    /**
     * 모든 상품 관련 캐시 무효화
     * - 상품 정보가 변경될 때 호출
     */
    @CacheEvict(value = { "bestProducts", "popularProducts" }, allEntries = true)
    public void evictAllProductCaches() {
        log.info("모든 상품 캐시를 무효화합니다.");
    }

    /**
     * 베스트 상품 캐시만 무효화
     */
    @CacheEvict(value = "bestProducts", allEntries = true)
    public void evictBestProductsCache() {
        log.info("베스트 상품 캐시를 무효화합니다.");
    }

    /**
     * 카테고리 캐시 무효화
     * - 카테고리 정보가 변경될 때 호출
     */
    @CacheEvict(value = "categories", allEntries = true)
    public void evictCategoriesCache() {
        log.info("카테고리 캐시를 무효화합니다.");
    }

    /**
     * 특정 카테고리의 베스트 상품 캐시 무효화
     */
    public void evictCategoryBestProductsCache(Long categoryId) {
        var cache = cacheManager.getCache("bestProducts");
        if (cache != null) {
            // 해당 카테고리와 관련된 모든 캐시 키를 찾아서 무효화
            // 실제로는 캐시 구현체에 따라 다를 수 있음
            log.info("카테고리 {}의 베스트 상품 캐시를 무효화합니다.", categoryId);
            cache.evictIfPresent("category_" + categoryId + "_10");
            cache.evictIfPresent("category_" + categoryId + "_20");
        }
    }

    /**
     * 캐시 통계 정보 조회 (모니터링용)
     */
    public void logCacheStats() {
        log.info("=== 캐시 통계 정보 ===");

        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                log.info("캐시 이름: {}", cacheName);

                // Caffeine 캐시의 경우 상세 통계 정보 출력
                var nativeCache = cache.getNativeCache();
                if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                    var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                    var stats = caffeineCache.stats();

                    log.info("  - 요청 수: {}", stats.requestCount());
                    log.info("  - 히트 수: {}", stats.hitCount());
                    log.info("  - 미스 수: {}", stats.missCount());
                    log.info("  - 히트율: {:.2f}%", stats.hitRate() * 100);
                    log.info("  - 평균 로드 시간: {:.2f}ms", stats.averageLoadPenalty() / 1_000_000.0);
                    log.info("  - 캐시 크기: {}", caffeineCache.estimatedSize());
                } else {
                    log.info("  - 캐시 타입: {}", nativeCache.getClass().getSimpleName());
                }
            }
        });
    }

    /**
     * 캐시 통계 정보 반환 (API용)
     */
    public Object getCacheStats() {
        Map<String, Object> allStats = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Map<String, Object> cacheStats = new HashMap<>();
                
                if (cache instanceof com.cMall.feedShop.config.TieredCache) {
                    // 이중 캐시인 경우
                    var nativeCache = cache.getNativeCache();
                    if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                        var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                        var stats = caffeineCache.stats();
                        
                        cacheStats.put("type", "Tiered (Caffeine + Redis)");
                        cacheStats.put("l1RequestCount", stats.requestCount());
                        cacheStats.put("l1HitCount", stats.hitCount());
                        cacheStats.put("l1MissCount", stats.missCount());
                        cacheStats.put("l1HitRate", Math.round(stats.hitRate() * 10000.0) / 100.0);
                        cacheStats.put("l1AverageLoadTime", Math.round(stats.averageLoadPenalty() / 1_000_000.0 * 100.0) / 100.0);
                        cacheStats.put("l1Size", caffeineCache.estimatedSize());
                        cacheStats.put("redisEnabled", true);
                    }
                } else {
                    // 단일 캐시인 경우
                    var nativeCache = cache.getNativeCache();
                    if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache) {
                        var caffeineCache = (com.github.benmanes.caffeine.cache.Cache<?, ?>) nativeCache;
                        var stats = caffeineCache.stats();
                        
                        cacheStats.put("type", "Caffeine Only");
                        cacheStats.put("requestCount", stats.requestCount());
                        cacheStats.put("hitCount", stats.hitCount());
                        cacheStats.put("missCount", stats.missCount());
                        cacheStats.put("hitRate", Math.round(stats.hitRate() * 10000.0) / 100.0);
                        cacheStats.put("averageLoadTime", Math.round(stats.averageLoadPenalty() / 1_000_000.0 * 100.0) / 100.0);
                        cacheStats.put("size", caffeineCache.estimatedSize());
                        cacheStats.put("redisEnabled", false);
                    } else {
                        cacheStats.put("type", nativeCache.getClass().getSimpleName());
                        cacheStats.put("size", "N/A");
                        cacheStats.put("redisEnabled", false);
                    }
                }
                
                allStats.put(cacheName, cacheStats);
            }
        });
        
        return allStats;
    }
}