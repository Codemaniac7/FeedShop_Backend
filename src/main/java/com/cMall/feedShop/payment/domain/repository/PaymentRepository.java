package com.cMall.feedShop.payment.domain.repository;

import com.cMall.feedShop.payment.domain.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
