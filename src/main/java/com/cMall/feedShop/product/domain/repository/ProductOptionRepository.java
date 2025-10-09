package com.cMall.feedShop.product.domain.repository;

import com.cMall.feedShop.product.domain.enums.Color;
import com.cMall.feedShop.product.domain.enums.Gender;
import com.cMall.feedShop.product.domain.enums.Size;
import com.cMall.feedShop.product.domain.model.ProductOption;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    Optional<ProductOption> findByOptionId(Long optionId);

    @EntityGraph(attributePaths = {"product", "product.store", "product.category"})
    List<ProductOption> findAllByOptionIdIn(Set<Long> optionIds);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT po FROM ProductOption po WHERE po.optionId IN :optionIds")
    List<ProductOption> findAllByOptionIdInWithPessimisticLock(Set<Long> optionIds);

    boolean existsByProduct_ProductIdAndGenderAndSizeAndColor(
            Long productId,
            Gender gender,
            Size size,
            Color color
    );
}
