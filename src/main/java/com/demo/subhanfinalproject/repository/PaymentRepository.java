package com.demo.subhanfinalproject.repository;

import com.demo.subhanfinalproject.model.entity.PaymentEntity;
import com.demo.subhanfinalproject.model.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
    List<PaymentEntity> findAllByPaymentStatus(PaymentStatus paymentStatus);

    boolean existsByCustomerIdAndFromCurrentAccountIsNotNullAndCreatedAtAfter(Long customerId, LocalDateTime startOfDay);

    boolean existsByCustomerIdAndFromCardIsNotNullAndCreatedAtAfter(Long customerId, LocalDateTime startOfDay);

    List<PaymentEntity> findAllByCustomerIdAndPaymentStatusAndProcessedAtAfter(Long customerId, PaymentStatus paymentStatus, LocalDateTime oneMonthAgo);

    List<PaymentEntity> findTop100ByFromCardIdAndPaymentStatusOrderByProcessedAtDesc(Long cardId, PaymentStatus status);

    List<PaymentEntity> findTop100ByFromCurrentAccountIdAndPaymentStatusOrderByProcessedAtDesc(Long accountId, PaymentStatus status);
}
