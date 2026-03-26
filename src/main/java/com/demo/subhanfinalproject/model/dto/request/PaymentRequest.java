package com.demo.subhanfinalproject.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private String from;
    private String to;
    private BigDecimal amount;
}
