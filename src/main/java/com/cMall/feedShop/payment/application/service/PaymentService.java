package com.cMall.feedShop.payment.application.service;

import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.order.domain.enums.OrderStatus;
import com.cMall.feedShop.order.domain.exception.OrderException;
import com.cMall.feedShop.order.domain.model.Order;
import com.cMall.feedShop.order.domain.repository.OrderRepository;
import com.cMall.feedShop.payment.application.dto.request.PaymentRequestDto;
import com.cMall.feedShop.payment.application.dto.response.PaymentResponseDto;
import com.cMall.feedShop.payment.domain.model.Payment;
import com.cMall.feedShop.payment.domain.model.PaymentStatus;
import com.cMall.feedShop.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        Order order = orderRepository.findByIdWithPessimisticLock(request.getOrderId())
                .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        // Simulate external payment processing
        boolean paymentSuccess = simulateExternalPayment(request.getPaymentMethod());

        Payment payment = createPayment(order, request.getPaymentMethod(), paymentSuccess);
        paymentRepository.save(payment);

        if (paymentSuccess) {
            order.updateStatus(OrderStatus.PAID);
        } else {
            order.updateStatus(OrderStatus.PAYMENT_FAILED);
        }

        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .orderId(order.getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .build();
    }

    private Payment createPayment(Order order, String paymentMethod, boolean paymentSuccess) {
        return Payment.builder()
                .order(order)
                .paymentMethod(paymentMethod)
                .amount(order.getFinalPrice())
                .status(paymentSuccess ? PaymentStatus.COMPLETED : PaymentStatus.FAILED)
                .transactionId(paymentSuccess ? UUID.randomUUID().toString() : null)
                .build();
    }

    private boolean simulateExternalPayment(String paymentMethod) {
        // In a real application, this would involve calling an external payment gateway
        return "CARD".equalsIgnoreCase(paymentMethod);
    }
}
