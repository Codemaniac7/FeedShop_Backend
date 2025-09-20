package com.cMall.feedShop.product.application.utils;

/**
 * 캐시 키 생성 유틸리티
 * 캐시 키 충돌을 방지하고 일관된 키 생성 전략을 제공
 */
public class CacheKeyGenerator {
    
    private static final String DELIMITER = "_";
    private static final String PREFIX_ALL = "all";
    private static final String PREFIX_CATEGORY = "category";
    private static final String PREFIX_PAGE = "page";
    
    /**
     * 전체 베스트 상품 캐시 키 생성
     * 
     * @param limit 조회할 상품 수
     * @param inStockOnly 재고 필터 여부
     * @return 캐시 키 (예: "all_limit_20_stock_true")
     */
    public static String generateBestProductsKey(int limit, boolean inStockOnly) {
        return String.join(DELIMITER, 
                PREFIX_ALL, 
                "limit", String.valueOf(limit),
                "stock", String.valueOf(inStockOnly));
    }
    
    /**
     * 카테고리별 베스트 상품 캐시 키 생성
     * 
     * @param categoryId 카테고리 ID
     * @param limit 조회할 상품 수
     * @param inStockOnly 재고 필터 여부
     * @return 캐시 키 (예: "category_1_limit_10_stock_true")
     */
    public static String generateCategoryBestProductsKey(Long categoryId, int limit, boolean inStockOnly) {
        return String.join(DELIMITER,
                PREFIX_CATEGORY, String.valueOf(categoryId),
                "limit", String.valueOf(limit),
                "stock", String.valueOf(inStockOnly));
    }
    
    /**
     * 베스트 상품 페이지 캐시 키 생성
     * 
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param inStockOnly 재고 필터 여부
     * @return 캐시 키 (예: "page_0_size_20_stock_true")
     */
    public static String generateBestProductsPageKey(int page, int size, boolean inStockOnly) {
        return String.join(DELIMITER,
                PREFIX_PAGE, String.valueOf(page),
                "size", String.valueOf(size),
                "stock", String.valueOf(inStockOnly));
    }
    
    /**
     * 카테고리 목록 캐시 키 생성
     * 
     * @return 캐시 키 ("all_categories")
     */
    public static String generateCategoriesKey() {
        return "all_categories";
    }
}