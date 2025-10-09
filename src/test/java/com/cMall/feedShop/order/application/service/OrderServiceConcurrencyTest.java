
package com.cMall.feedShop.order.application.service;

import com.cMall.feedShop.order.application.dto.request.DirectOrderCreateRequest;
import com.cMall.feedShop.order.application.dto.request.OrderItemRequest;
import com.cMall.feedShop.order.application.service.DirectOrderService;
import com.cMall.feedShop.order.domain.repository.OrderRepository;
import com.cMall.feedShop.product.domain.enums.Color;
import com.cMall.feedShop.product.domain.enums.DiscountType;
import com.cMall.feedShop.product.domain.enums.Gender;
import com.cMall.feedShop.product.domain.enums.ImageType;
import com.cMall.feedShop.product.domain.enums.Size;
import com.cMall.feedShop.product.domain.model.Product;
import com.cMall.feedShop.product.domain.model.ProductImage;
import com.cMall.feedShop.product.domain.model.ProductOption;
import com.cMall.feedShop.product.domain.repository.ProductImageRepository;
import com.cMall.feedShop.product.domain.repository.ProductOptionRepository;
import com.cMall.feedShop.product.domain.repository.ProductRepository;
import com.cMall.feedShop.user.domain.enums.UserRole;
import com.cMall.feedShop.user.domain.model.User;
import com.cMall.feedShop.user.domain.repository.UserRepository;
import com.cMall.feedShop.user.domain.repository.UserActivityRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("주문 서비스 동시성 테스트")
class OrderServiceConcurrencyTest {

    @Autowired
    private DirectOrderService directOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository productOptionRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    private User testUser;
    private Product testProduct;
    private ProductImage testProductImage;
    private ProductOption testProductOption;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 정리
        orderRepository.deleteAll();
        productImageRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        // 1. 테스트용 사용자 생성 및 저장
        testUser = userRepository.save(new User("concurrentUser", "password", "concurrent@test.com", UserRole.USER));

        // 2. 테스트용 상품 생성 및 저장
        testProduct = Product.builder()
                .name("동시성 테스트 상품")
                .price(BigDecimal.valueOf(1000))
                .description("재고 차감 동시성 테스트를 위한 상품")
                .discountType(DiscountType.NONE)
                .discountValue(BigDecimal.ZERO)
                .build();
        productRepository.save(testProduct);

        // 3. 테스트용 상품 이미지 생성 및 저장
        testProductImage = productImageRepository.save(new ProductImage("http://example.com/image.jpg", ImageType.MAIN, testProduct));

        // 4. 테스트용 상품 옵션 생성 및 저장 (초기 재고: 10개)
        testProductOption = new ProductOption(Gender.UNISEX, Size.SIZE_250, Color.BLACK, 10, testProduct);
        productOptionRepository.save(testProductOption);
    }

    @AfterEach
    void tearDown() {
        // 테스트 데이터 정리
        orderRepository.deleteAll();
        productImageRepository.deleteAll();
        productOptionRepository.deleteAll();
        productRepository.deleteAll();
        userActivityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("여러 사용자가 동시에 주문할 때 비관적 락이 재고를 올바르게 차감하는지 테스트")
    void testConcurrentOrderStockDeduction() throws InterruptedException {
        // Given: 스레드 30개, 각 스레드가 1개씩 주문 (총 30개 주문 시도)
        int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successfulOrders = new AtomicInteger(0);
        AtomicInteger failedOrders = new AtomicInteger(0);

        // When: 30개의 스레드가 동시에 주문 생성 요청
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // 주문 요청 생성
                    DirectOrderCreateRequest request = createDirectOrderRequest(testProductOption.getOptionId());
                    directOrderService.createDirectOrder(request, testUser.getLoginId());
                    successfulOrders.incrementAndGet();
                } catch (Exception e) {
                    // 재고 부족 등 예외 발생 시 실패 카운트 증가
                    e.printStackTrace();
                    failedOrders.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        latch.await();
        executorService.shutdown();

        // Then: 결과 검증
        // 1. 최종 재고 확인
        ProductOption finalOption = productOptionRepository.findById(testProductOption.getOptionId()).orElseThrow();
        assertThat(finalOption.getStock()).isEqualTo(0); // 재고는 0이 되어야 함

        // 2. 주문 성공/실패 횟수 확인
        long totalOrdersInDb = orderRepository.count();
        assertThat(totalOrdersInDb).isEqualTo(10); // DB에 저장된 주문은 정확히 10개여야 함
        assertThat(successfulOrders.get()).isEqualTo(10); // 성공한 주문은 10개
        assertThat(failedOrders.get()).isEqualTo(20); // 실패한 주문은 20개
    }

    private DirectOrderCreateRequest createDirectOrderRequest(Long optionId) {
        OrderItemRequest item = new OrderItemRequest();
        item.setOptionId(optionId);
        item.setImageId(testProductImage.getImageId());
        item.setQuantity(1);

        DirectOrderCreateRequest request = new DirectOrderCreateRequest();
        request.setItems(List.of(item));
        request.setDeliveryAddress("테스트 주소");
        request.setDeliveryDetailAddress("상세 주소");
        request.setPaymentMethod("CARD");
        request.setPostalCode("12345");
        request.setRecipientName("테스트 수령인");
        request.setRecipientPhone("010-1234-5678");
        request.setUsedPoints(0);
        return request;
    }
}
