package com.cMall.feedShop.payment.domain.model;

import com.cMall.feedShop.common.BaseTimeEntity;
import com.cMall.feedShop.order.domain.model.Order;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Column(name = "transaction_id")
    private String transactionId;

    @Builder
    public Payment(Order order, String paymentMethod, BigDecimal amount, PaymentStatus status, String transactionId) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }
}
