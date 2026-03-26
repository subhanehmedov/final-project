package com.demo.subhanfinalproject.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.demo.subhanfinalproject.model.enums.Currency;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "current_accounts")
public class CurrentAccountEntity extends BaseEntity {
    private String number;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currentCurrency;

    @Enumerated(EnumType.STRING)
    private Currency baseCurrency;

    private LocalDateTime expirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
