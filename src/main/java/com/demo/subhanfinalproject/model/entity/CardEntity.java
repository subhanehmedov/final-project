package com.demo.subhanfinalproject.model.entity;

import com.demo.subhanfinalproject.model.enums.Currency;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
public class CardEntity extends BaseEntity {
    private String number;
    private String cvv;
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
