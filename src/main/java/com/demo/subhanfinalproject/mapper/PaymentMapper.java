package com.demo.subhanfinalproject.mapper;

import com.demo.subhanfinalproject.model.dto.response.PaymentResponse;
import com.demo.subhanfinalproject.model.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponse toResponse(PaymentEntity paymentEntity) {
        PaymentResponse paymentResponse = new PaymentResponse();
        String from;
        String to;
        if (paymentEntity.getFromCard() != null) {
            from = paymentEntity.getFromCard().getNumber();
            if (paymentEntity.getToCard() != null) {
                to = paymentEntity.getToCard().getNumber();
            } else {
                to = paymentEntity.getToCurrentAccount().getNumber();
            }
        } else {
            from = paymentEntity.getFromCurrentAccount().getNumber();
            if (paymentEntity.getToCurrentAccount() != null) {
                to = paymentEntity.getToCurrentAccount().getNumber();
            } else {
                to = paymentEntity.getToCard().getNumber();
            }
        }
        paymentResponse.setFrom(from);
        paymentResponse.setTo(to);
        paymentResponse.setAmount(paymentEntity.getAmount());
        paymentResponse.setCurrency(paymentEntity.getCurrency());
        paymentResponse.setProcessedAt(paymentEntity.getProcessedAt());
        paymentResponse.setPaymentStatus(paymentEntity.getPaymentStatus());
        return paymentResponse;
    }
}
