package com.cMall.feedShop.payment.application.dto.response;

import com.cMall.feedShop.payment.domain.model.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class PaymentResponseDto {
    private Long paymentId;
    private Long orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;

    @Builder
    public PaymentResponseDto(Long paymentId, Long orderId, BigDecimal amount, PaymentStatus status, String transactionId) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
    }
}
