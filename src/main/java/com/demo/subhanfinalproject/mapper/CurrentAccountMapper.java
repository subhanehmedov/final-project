package com.demo.subhanfinalproject.mapper;

import com.demo.subhanfinalproject.model.dto.response.CurrentAccountResponse;
import com.demo.subhanfinalproject.model.entity.CurrentAccountEntity;
import org.springframework.stereotype.Component;

@Component
public class CurrentAccountMapper {
    public CurrentAccountResponse toResponse(CurrentAccountEntity currentAccountEntity) {
        CurrentAccountResponse currentAccountResponse = new CurrentAccountResponse();
        currentAccountResponse.setNumber(currentAccountEntity.getNumber());
        currentAccountResponse.setCurrency(currentAccountEntity.getCurrency());
        currentAccountResponse.setBalance(currentAccountEntity.getBalance());
        currentAccountResponse.setExpirationDate(currentAccountEntity.getExpirationDate());
        currentAccountResponse.setCustomerId(currentAccountEntity.getCustomer().getId());
        return currentAccountResponse;
    }
}
