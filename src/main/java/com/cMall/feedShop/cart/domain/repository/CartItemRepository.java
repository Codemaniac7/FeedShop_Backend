package com.cMall.feedShop.cart.domain.repository;

import com.cMall.feedShop.cart.domain.model.Cart;
import com.cMall.feedShop.cart.domain.model.CartItem;
import com.cMall.feedShop.cart.infrastructure.repository.CartItemQueryRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemQueryRepository {
    Optional<CartItem> findByCartAndOptionIdAndImageId(Cart cart, Long optionId, Long imageId);

    List<CartItem> findAllByCartItemIdInAndCartUserId(List<Long> cartItemIds, Long userId);
}
