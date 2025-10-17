package com.cMall.feedShop.order.domain.exception;

import com.cMall.feedShop.common.exception.ErrorCode;

public class PaymentException extends RuntimeException {
    private final ErrorCode errorCode;

    // 기본 메시지를 사용하는 생성자
    public PaymentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    // 커스텀 메시지를 사용하는 생성자
    public PaymentException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // 원인(cause)을 포함하는 생성자
    public PaymentException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
