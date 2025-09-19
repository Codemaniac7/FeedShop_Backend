package com.cMall.feedShop.product.application.service;

import com.cMall.feedShop.product.application.dto.response.CategoryResponse;
import com.cMall.feedShop.product.domain.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryReadService {
    private final CategoryRepository categoryRepository;

    /**
     * 모든 카테고리 조회 (캐시 적용)
     * - 카테고리는 거의 변경되지 않으므로 캐싱 효과가 높음
     * - 30분 캐시 유지
     */
    @Cacheable(value = "categories", key = "T(com.cMall.feedShop.product.application.utils.CacheKeyGenerator).generateCategoriesKey()")
    public List<CategoryResponse> getAllCategories() {
        log.info("카테고리 목록을 데이터베이스에서 조회합니다.");
        return categoryRepository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }
}
