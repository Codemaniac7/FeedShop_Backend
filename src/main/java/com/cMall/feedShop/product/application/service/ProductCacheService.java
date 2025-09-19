package com.cMall.feedShop.product.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {
    private final CacheManager cacheManager;

    /**
     * 모든 상품 관련 캐시 무효화
     * - 상품 정보가 변경될 때 호출
     */
    @CacheEvict(value = {"bestProducts", "popularProducts"}, allEntries = true)
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
        var caffeineCacheManager = cacheManager;
        log.info("=== 캐시 통계 정보 ===");
        
        caffeineCacheManager.getCacheNames().forEach(cacheName -> {
            var cache = caffeineCacheManager.getCache(cacheName);
            if (cache != null) {
                log.info("캐시 이름: {}", cacheName);
                // Caffeine 캐시의 경우 통계 정보 출력 가능
                // cache.getNativeCache() 를 통해 접근 가능
            }
        });
    }
}