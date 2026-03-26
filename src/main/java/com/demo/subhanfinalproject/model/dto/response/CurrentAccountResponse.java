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
public class CurrentAccountResponse {
    private Long id;
    private String number;
    private BigDecimal balance;
    private Currency currency;
    private LocalDateTime expirationDate;
    private Long customerId;
}
