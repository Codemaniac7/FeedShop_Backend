package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.request.ProductSearchRequest;
import com.cMall.feedShop.product.application.dto.response.ProductListResponse;
import com.cMall.feedShop.product.application.dto.response.ProductPageResponse;
import com.cMall.feedShop.product.application.utils.CacheKeyGenerator;
import com.cMall.feedShop.product.domain.enums.ProductSortType;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BestProductService {
        private final ProductRepository productRepository;
        private final ProductMapper productMapper;

        /**
         * 베스트 상품 목록 조회 (캐시 적용)
         * - 인기순으로 정렬된 상위 상품들을 캐싱
         * - 30분 캐시 유지
         * 
         * @param limit       조회할 상품 수
         * @param inStockOnly 재고 있는 상품만 조회할지 여부
         * @return 베스트 상품 목록
         */
        @Cacheable(value = "bestProducts", key = "T(com.cMall.feedShop.product.application.utils.CacheKeyGenerator).generateBestProductsKey(#limit, #inStockOnly)")
        public List<ProductListResponse> getBestProducts(int limit, boolean inStockOnly) {
                log.info("베스트 상품 {}개를 데이터베이스에서 조회합니다. (재고필터: {})", limit, inStockOnly);

                ProductSearchRequest request = ProductSearchRequest.builder()
                                .inStockOnly(inStockOnly ? true : null) // null이면 재고 필터링 안함
                                .build();

                Pageable pageable = PageRequest.of(0, limit);
                Page<Product> productPage = productRepository.findWithAllConditions(
                                request, ProductSortType.POPULAR, pageable);

                return productPage.getContent()
                                .stream()
                                .map(productMapper::toListResponse)
                                .toList();
        }

        /**
         * 베스트 상품 목록 조회 (재고 있는 상품만, 기본값)
         */
        public List<ProductListResponse> getBestProducts(int limit) {
                return getBestProducts(limit, true);
        }

        /**
         * 카테고리별 베스트 상품 조회 (캐시 적용)
         * 
         * @param categoryId  카테고리 ID
         * @param limit       조회할 상품 수
         * @param inStockOnly 재고 있는 상품만 조회할지 여부
         * @return 카테고리별 베스트 상품 목록
         */
        @Cacheable(value = "bestProducts", key = "T(com.cMall.feedShop.product.application.utils.CacheKeyGenerator).generateCategoryBestProductsKey(#categoryId, #limit, #inStockOnly)")
        public List<ProductListResponse> getBestProductsByCategory(Long categoryId, int limit, boolean inStockOnly) {
                log.info("카테고리 {}의 베스트 상품 {}개를 데이터베이스에서 조회합니다. (재고필터: {})", categoryId, limit, inStockOnly);

                ProductSearchRequest request = ProductSearchRequest.builder()
                                .categoryId(categoryId)
                                .inStockOnly(inStockOnly ? true : null)
                                .build();

                Pageable pageable = PageRequest.of(0, limit);
                Page<Product> productPage = productRepository.findWithAllConditions(
                                request, ProductSortType.POPULAR, pageable);

                return productPage.getContent()
                                .stream()
                                .map(productMapper::toListResponse)
                                .toList();
        }

        /**
         * 카테고리별 베스트 상품 조회 (재고 있는 상품만, 기본값)
         */
        public List<ProductListResponse> getBestProductsByCategory(Long categoryId, int limit) {
                return getBestProductsByCategory(categoryId, limit, true);
        }

        /**
         * 전체 베스트 상품 페이지 조회 (캐시 적용)
         * 
         * @param page        페이지 번호
         * @param size        페이지 크기
         * @param inStockOnly 재고 있는 상품만 조회할지 여부
         * @return 베스트 상품 페이지 응답
         */
        @Cacheable(value = "popularProducts", key = "T(com.cMall.feedShop.product.application.utils.CacheKeyGenerator).generateBestProductsPageKey(#page, #size, #inStockOnly)")
        public ProductPageResponse getBestProductsPage(int page, int size, boolean inStockOnly) {
                log.info("베스트 상품 페이지 (page: {}, size: {})를 데이터베이스에서 조회합니다. (재고필터: {})", page, size, inStockOnly);

                ProductSearchRequest request = ProductSearchRequest.builder()
                                .inStockOnly(inStockOnly ? true : null)
                                .build();

                // 총 개수 조회
                long totalElements = productRepository.countWithAllConditions(request);

                // 페이지 정보 생성
                Pageable pageable = PageRequest.of(page, size);

                // 인기순으로 상품 조회
                Page<Product> productPage = productRepository.findWithAllConditions(
                                request, ProductSortType.POPULAR, pageable);

                // 응답 변환
                Page<ProductListResponse> responsePage = productPage.map(productMapper::toListResponse);

                return ProductPageResponse.of(responsePage);
        }

        /**
         * 전체 베스트 상품 페이지 조회 (재고 있는 상품만, 기본값)
         */
        public ProductPageResponse getBestProductsPage(int page, int size) {
                return getBestProductsPage(page, size, true);
        }
}