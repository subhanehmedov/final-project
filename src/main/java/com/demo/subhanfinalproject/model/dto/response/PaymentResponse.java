package com.demo.subhanfinalproject.model.dto.response;

import com.demo.subhanfinalproject.model.enums.Currency;
import com.demo.subhanfinalproject.model.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String from;
    private String to;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private Currency currency;
    private LocalDateTime processedAt;
}
