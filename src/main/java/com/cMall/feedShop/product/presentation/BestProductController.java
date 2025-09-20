package com.cMall.feedShop.product.presentation;

import com.cMall.feedShop.common.aop.ApiResponseFormat;
import com.cMall.feedShop.common.dto.ApiResponse;
import com.cMall.feedShop.product.application.dto.response.ProductListResponse;
import com.cMall.feedShop.product.application.dto.response.ProductPageResponse;
import com.cMall.feedShop.product.application.service.BestProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "베스트 상품", description = "베스트/인기 상품 조회 관련 API")
@RestController
@RequestMapping("/api/products/best")
@RequiredArgsConstructor
public class BestProductController {
    private final BestProductService bestProductService;

    @Operation(
            summary = "베스트 상품 목록 조회",
            description = "인기순으로 정렬된 베스트 상품 목록을 조회합니다. 캐싱이 적용되어 빠른 응답을 제공합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "베스트 상품 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))
            )
    })
    @GetMapping
    @ApiResponseFormat(message = "베스트 상품 목록을 성공적으로 조회했습니다.")
    public ApiResponse<List<ProductListResponse>> getBestProducts(
            @Parameter(description = "조회할 상품 수", example = "20")
            @RequestParam(defaultValue = "20") int limit,
            @Parameter(description = "재고 있는 상품만 조회", example = "true")
            @RequestParam(defaultValue = "true") boolean inStockOnly) {
        List<ProductListResponse> data = bestProductService.getBestProducts(limit, inStockOnly);
        return ApiResponse.success(data);
    }

    @Operation(
            summary = "카테고리별 베스트 상품 조회",
            description = "특정 카테고리의 베스트 상품 목록을 조회합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "카테고리별 베스트 상품 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductListResponse.class))
            )
    })
    @GetMapping("/category/{categoryId}")
    @ApiResponseFormat(message = "카테고리별 베스트 상품을 성공적으로 조회했습니다.")
    public ApiResponse<List<ProductListResponse>> getBestProductsByCategory(
            @Parameter(description = "카테고리 ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @Parameter(description = "조회할 상품 수", example = "10")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "재고 있는 상품만 조회", example = "true")
            @RequestParam(defaultValue = "true") boolean inStockOnly) {
        List<ProductListResponse> data = bestProductService.getBestProductsByCategory(categoryId, limit, inStockOnly);
        return ApiResponse.success(data);
    }

    @Operation(
            summary = "베스트 상품 페이지 조회",
            description = "베스트 상품을 페이징하여 조회합니다. 베스트 상품 전용 페이지에서 사용합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "베스트 상품 페이지 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProductPageResponse.class))
            )
    })
    @GetMapping("/page")
    @ApiResponseFormat(message = "베스트 상품 페이지를 성공적으로 조회했습니다.")
    public ApiResponse<ProductPageResponse> getBestProductsPage(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "재고 있는 상품만 조회", example = "true")
            @RequestParam(defaultValue = "true") boolean inStockOnly) {
        ProductPageResponse data = bestProductService.getBestProductsPage(page, size, inStockOnly);
        return ApiResponse.success(data);
    }
}