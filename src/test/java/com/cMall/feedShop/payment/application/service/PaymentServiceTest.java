package com.cMall.feedShop.payment.application.service;

import com.cMall.feedShop.common.exception.ErrorCode;
import com.cMall.feedShop.order.domain.enums.OrderStatus;
import com.cMall.feedShop.order.domain.exception.OrderException;
import com.cMall.feedShop.order.domain.exception.PaymentException;
import com.cMall.feedShop.order.domain.model.Order;
import com.cMall.feedShop.order.domain.repository.OrderRepository;
import com.cMall.feedShop.payment.application.dto.request.PaymentRequestDto;
import com.cMall.feedShop.payment.application.dto.response.PaymentResponseDto;
import com.cMall.feedShop.payment.domain.model.Payment;
import com.cMall.feedShop.payment.domain.model.PaymentStatus;
import com.cMall.feedShop.payment.domain.repository.PaymentRepository;
import com.cMall.feedShop.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 서비스 테스트")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    private User testUser;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password123", "test@test.com", null);
        ReflectionTestUtils.setField(testUser, "id", 1L);

        testOrder = Order.builder()
                .user(testUser)
                .status(OrderStatus.ORDERED)
                .totalPrice(BigDecimal.valueOf(100000))
                .finalPrice(BigDecimal.valueOf(103000))
                .deliveryFee(BigDecimal.valueOf(3000))
                .usedPoints(0)
                .earnedPoints(500)
                .deliveryAddress("서울시 강남구")
                .deliveryDetailAddress("테스트동 123-45")
                .postalCode("12345")
                .recipientName("홍길동")
                .recipientPhone("010-1234-5678")
                .deliveryMessage("문 앞에 놓아주세요")
                .build();
        ReflectionTestUtils.setField(testOrder, "orderId", 1L);
    }

    @Test
    @Transactional
    @DisplayName("결제 성공")
    void processPayment_Success() {
        // Given
        PaymentRequestDto request = new PaymentRequestDto(1L, "CARD");
        given(orderRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(testOrder));
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "paymentId", 1L);
            return payment;
        });

        // When
        PaymentResponseDto response = paymentService.processPayment(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getPaymentId()).isEqualTo(1L);
        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualTo(testOrder.getFinalPrice());
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(testOrder.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(paymentRepository).save(any(Payment.class));
    }

    @Test
    @Transactional
    @DisplayName("결제 실패 - 주문을 찾을 수 없음")
    void processPayment_OrderNotFound() {
        // Given
        PaymentRequestDto request = new PaymentRequestDto(999L, "CARD");
        given(orderRepository.findByIdWithPessimisticLock(999L)).willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(OrderException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @Transactional
    @DisplayName("결제 실패 - 결제 수단이 유효하지 않음")
    void processPayment_PaymentFailure() {
        // Given
        PaymentRequestDto request = new PaymentRequestDto(1L, "INVALID_METHOD");
        given(orderRepository.findByIdWithPessimisticLock(1L)).willReturn(Optional.of(testOrder));
        given(paymentRepository.save(any(Payment.class))).willAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            ReflectionTestUtils.setField(payment, "paymentId", 1L);
            return payment;
        });

        // When & Then
        assertThatThrownBy(() -> paymentService.processPayment(request))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PAYMENT_FAILED);
    }
}
