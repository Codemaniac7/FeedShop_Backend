package com.cMall.feedShop.order.domain.event;

import org.springframework.context.ApplicationEvent;

public class OrderCompletedEvent extends ApplicationEvent {
    private final Long userId;
    private final Long orderId;

    // 생성자
    public OrderCompletedEvent(Object source, Long userId, Long orderId) {
        super(source);
        this.userId = userId;
        this.orderId = orderId;
    }

    // Getter
    public Long getUserId() {
        return userId;
    }

    public Long getOrderId() {
        return orderId;
    }
}
