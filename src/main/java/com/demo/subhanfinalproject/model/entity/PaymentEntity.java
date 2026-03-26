package com.demo.subhanfinalproject.model.entity;

import com.demo.subhanfinalproject.model.enums.Currency;
import com.demo.subhanfinalproject.model.enums.PaymentStatus;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class PaymentEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private Currency currency;
    private LocalDateTime processedAt;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_card_id")
    private CardEntity fromCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_card_id")
    private CardEntity toCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_current_account_id")
    private CurrentAccountEntity fromCurrentAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_current_account_id")
    private CurrentAccountEntity toCurrentAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;
}
