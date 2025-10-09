package com.cMall.feedShop.payment.presentation;

import com.cMall.feedShop.payment.application.dto.request.PaymentRequestDto;
import com.cMall.feedShop.payment.application.dto.response.PaymentResponseDto;
import com.cMall.feedShop.payment.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<PaymentResponseDto> processPayment(@RequestBody PaymentRequestDto request) {
        PaymentResponseDto response = paymentService.processPayment(request);
        return ResponseEntity.ok(response);
    }
}
