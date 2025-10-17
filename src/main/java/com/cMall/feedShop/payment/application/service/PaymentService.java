package com.cMall.feedShop.payment.application.service;

import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.order.domain.enums.OrderStatus;
import com.cMall.feedShop.order.domain.exception.ExternalPaymentException;
import com.cMall.feedShop.order.domain.exception.OrderException;
import com.cMall.feedShop.order.domain.exception.PaymentException;
import com.cMall.feedShop.order.domain.model.Order;
import com.cMall.feedShop.order.domain.repository.OrderRepository;
import com.cMall.feedShop.payment.application.dto.request.PaymentRequestDto;
import com.cMall.feedShop.payment.application.dto.response.PaymentResponseDto;
import com.cMall.feedShop.payment.domain.model.Payment;
import com.cMall.feedShop.payment.domain.model.PaymentStatus;
import com.cMall.feedShop.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        Order order = orderRepository.findByIdWithPessimisticLock(request.getOrderId())
                .orElseThrow(() -> new OrderException(ErrorCode.ORDER_NOT_FOUND));

        int maxRetries = 3;
        int retryCount = 0;
        long delayMillis = 1000; // 1초 대기

        while (retryCount < maxRetries) {
            // 외부 결제 처리 시뮬레이션
            try {
                boolean paymentSuccess = simulateExternalPayment(request.getPaymentMethod());

                Payment payment = createPayment(order, request.getPaymentMethod(), paymentSuccess);
                paymentRepository.save(payment);

                if (!paymentSuccess) {
                    throw new PaymentException(ErrorCode.PAYMENT_FAILED, "결제 처리에 실패했습니다.");
                }

                order.updateStatus(OrderStatus.PAID);

                return PaymentResponseDto.builder()
                        .paymentId(payment.getPaymentId())
                        .orderId(order.getOrderId())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .transactionId(payment.getTransactionId())
                        .build();

            } catch (ExternalPaymentException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("외부 결제 시스템 최종 실패 - orderId: {}, 시도 횟수: {}", request.getOrderId(), retryCount, e);
                    throw new PaymentException(ErrorCode.EXTERNAL_PAYMENT_ERROR, "외부 결제 시스템 호출 실패 (최대 재시도 초과)", e);
                }
                log.warn("외부 결제 시스템 오류, 재시도 시도 - orderId: {}, 재시도 횟수: {}", request.getOrderId(), retryCount, e);
                try {
                    Thread.sleep(delayMillis * retryCount); // 지수 백오프 비슷한 대기
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new PaymentException(ErrorCode.EXTERNAL_PAYMENT_ERROR, "재시도 대기 중 오류", ie);
                }
            }
        }

        throw new PaymentException(ErrorCode.EXTERNAL_PAYMENT_ERROR, "외부 결제 시스템 호출 실패");
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
