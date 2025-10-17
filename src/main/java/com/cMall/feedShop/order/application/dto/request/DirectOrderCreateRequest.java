package com.cMall.feedShop.order.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DirectOrderCreateRequest extends OrderCreateRequest {
    @NotEmpty(message = "주문할 상품 목록은 1개 이상이어야 합니다.")
    @Valid
    @Schema(description = "주문할 상품 목록", required = true)
    private List<OrderItemRequest> items;

    @NotEmpty(message = "주문할 장바구니 아이템 ID는 1개 이상이어야 합니다.")
    @Schema(description = "주문할 CartItem ID 목록 (장바구니에서 주문 시 필요)", required = true)
    private List<Long> cartItemIds;
}
