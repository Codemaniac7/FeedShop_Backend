package com.cMall.feedShop.order.domain.exception;

public class ExternalPaymentException extends RuntimeException {
    public ExternalPaymentException(String message) {
        super(message);
    }
}
