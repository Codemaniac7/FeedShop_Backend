package com.cMall.feedShop.product.presentation;

import com.cMall.feedShop.common.aop.ApiResponseFormat;
import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.product.application.service.ProductCacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "캐시 관리", description = "캐시 관리 관련 API (관리자용)")
@RestController
@RequestMapping("/api/admin/cache")
@RequiredArgsConstructor
public class CacheManagementController {
    private final ProductCacheService productCacheService;

    @Operation(
            summary = "모든 상품 캐시 무효화",
            description = "베스트 상품, 인기 상품 등 모든 상품 관련 캐시를 무효화합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "캐시 무효화 성공"
            )
    })
    @DeleteMapping("/products")
    @ApiResponseFormat(message = "모든 상품 캐시를 성공적으로 무효화했습니다.")
    public ApiResponse<Void> evictAllProductCaches() {
        productCacheService.evictAllProductCaches();
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "베스트 상품 캐시 무효화",
            description = "베스트 상품 관련 캐시만 무효화합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "베스트 상품 캐시 무효화 성공"
            )
    })
    @DeleteMapping("/products/best")
    @ApiResponseFormat(message = "베스트 상품 캐시를 성공적으로 무효화했습니다.")
    public ApiResponse<Void> evictBestProductsCache() {
        productCacheService.evictBestProductsCache();
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "카테고리 캐시 무효화",
            description = "카테고리 목록 캐시를 무효화합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "카테고리 캐시 무효화 성공"
            )
    })
    @DeleteMapping("/categories")
    @ApiResponseFormat(message = "카테고리 캐시를 성공적으로 무효화했습니다.")
    public ApiResponse<Void> evictCategoriesCache() {
        productCacheService.evictCategoriesCache();
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "특정 카테고리 베스트 상품 캐시 무효화",
            description = "특정 카테고리의 베스트 상품 캐시를 무효화합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "카테고리별 베스트 상품 캐시 무효화 성공"
            )
    })
    @DeleteMapping("/products/best/category/{categoryId}")
    @ApiResponseFormat(message = "카테고리별 베스트 상품 캐시를 성공적으로 무효화했습니다.")
    public ApiResponse<Void> evictCategoryBestProductsCache(
            @Parameter(description = "카테고리 ID", required = true, example = "1")
            @PathVariable Long categoryId) {
        productCacheService.evictCategoryBestProductsCache(categoryId);
        return ApiResponse.success(null);
    }

    @Operation(
            summary = "캐시 통계 정보 조회",
            description = "현재 캐시 상태와 통계 정보를 로그로 출력합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "캐시 통계 정보 조회 성공"
            )
    })
    @GetMapping("/stats")
    @ApiResponseFormat(message = "캐시 통계 정보를 성공적으로 조회했습니다.")
    public ApiResponse<Void> getCacheStats() {
        productCacheService.logCacheStats();
        return ApiResponse.success(null);
    }
}