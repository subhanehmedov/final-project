package com.demo.subhanfinalproject.model.dto.response;

import com.demo.subhanfinalproject.model.enums.Currency;
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
public class CardResponse {
    private String number;
    private String cvv;
    private BigDecimal balance;
    private LocalDateTime expirationDate;
    private Currency currency;
    private Long customerId;
}
