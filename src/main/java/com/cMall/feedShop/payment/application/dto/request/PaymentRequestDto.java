package com.cMall.feedShop.payment.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequestDto {
    private Long orderId;
    private String paymentMethod;

    public PaymentRequestDto(Long orderId, String paymentMethod) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
    }
}
