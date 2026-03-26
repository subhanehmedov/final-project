package com.demo.subhanfinalproject.model.entity;

import com.demo.subhanfinalproject.model.enums.Currency;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentAccountEntity extends BaseEntity {
    private String number;
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private LocalDateTime expirationDate;

}
