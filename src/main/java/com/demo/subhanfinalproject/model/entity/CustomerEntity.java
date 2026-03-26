package com.demo.subhanfinalproject.model.entity;

import com.demo.subhanfinalproject.model.enums.CustomerStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity extends BaseEntity {
    private String name;
    private String surname;

    @Enumerated(EnumType.STRING)
    private CustomerStatus status;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "customer"
    )
    private List<CardEntity> cardEntities;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "customer"
    )
    private List<CurrentAccountEntity> currentAccounts;
}
