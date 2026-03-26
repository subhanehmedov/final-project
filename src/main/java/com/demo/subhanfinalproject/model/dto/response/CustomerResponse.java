package com.demo.subhanfinalproject.model.dto.response;

import com.demo.subhanfinalproject.model.enums.CustomerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private CustomerStatus status;
    private List<CardResponse> cards;
    private List<CurrentAccountResponse> currentAccounts;
}
