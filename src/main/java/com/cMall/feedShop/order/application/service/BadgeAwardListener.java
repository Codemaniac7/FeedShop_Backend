package com.cMall.feedShop.order.application.service;

import com.cMall.feedShop.order.domain.event.OrderCompletedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Component
public class BadgeAwardListener {
    private static final Logger log = LoggerFactory.getLogger(BadgeAwardListener.class);
    private final OrderHelper orderHelper;

    public BadgeAwardListener(OrderHelper orderHelper) {
        this.orderHelper = orderHelper;
    }

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOrderCompleted(OrderCompletedEvent event) {
        try {
            orderHelper.checkAndAwardBadgesAfterOrder(event.getUserId(), event.getOrderId());
            log.info("뱃지 수여 완료 - userId: {}, orderId: {}", event.getUserId(), event.getOrderId());
        } catch (Exception e) {
            log.error("뱃지 수여 실패 - userId: {}, orderId: {}, error: {}",
                    event.getUserId(), event.getOrderId(), e.getMessage(), e);
            // 필요 시 재시도 로직 또는 알림 추가
        }
    }
}
