package com.cMall.feedShop.product.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

/**
 * 상품 관련 캐시 무효화 전용 서비스
 * AOP 기반으로 자동 캐시 무효화 처리
 */
@Slf4j
@Service
public class ProductCacheEvictService {

    /**
     * 상품 생성 시 캐시 무효화
     * - 새로운 상품이 추가되면 베스트 상품 목록이 변경될 수 있음
     */
    @CacheEvict(value = {"bestProducts", "popularProducts"}, allEntries = true)
    public void evictOnProductCreate() {
        log.info("상품 생성으로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 수정 시 캐시 무효화
     * - 상품 정보 변경으로 인해 베스트 상품 순위가 변경될 수 있음
     */
    @CacheEvict(value = {"bestProducts", "popularProducts"}, allEntries = true)
    public void evictOnProductUpdate() {
        log.info("상품 수정으로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 삭제 시 캐시 무효화
     * - 상품이 삭제되면 베스트 상품 목록에서 제외되어야 함
     */
    @CacheEvict(value = {"bestProducts", "popularProducts"}, allEntries = true)
    public void evictOnProductDelete() {
        log.info("상품 삭제로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 재고 변경 시 캐시 무효화
     * - 재고 상태 변경으로 인해 재고 필터링된 베스트 상품 목록이 변경될 수 있음
     */
    @CacheEvict(value = {"bestProducts", "popularProducts"}, allEntries = true)
    public void evictOnStockChange() {
        log.info("상품 재고 변경으로 인한 캐시 무효화 실행");
    }

    /**
     * 상품 인기도 변경 시 캐시 무효화 (위시리스트, 주문 등)
     * - 인기도 변경으로 인해 베스트 상품 순위가 변경될 수 있음
     */
    @CacheEvict(value = {"bestProducts", "popularProducts"}, allEntries = true)
    public void evictOnPopularityChange() {
        log.info("상품 인기도 변경으로 인한 캐시 무효화 실행");
    }

    /**
     * 카테고리 변경 시 캐시 무효화
     * - 카테고리 정보 변경으로 인해 카테고리 목록과 카테고리별 베스트 상품이 변경될 수 있음
     */
    @CacheEvict(value = {"categories", "bestProducts"}, allEntries = true)
    public void evictOnCategoryChange() {
        log.info("카테고리 변경으로 인한 캐시 무효화 실행");
    }
}