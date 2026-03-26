package com.demo.subhanfinalproject.mapper;

import com.demo.subhanfinalproject.model.dto.response.CurrentAccountResponse;
import com.demo.subhanfinalproject.model.entity.CurrentAccountEntity;
import com.demo.subhanfinalproject.model.enums.Currency;
import com.demo.subhanfinalproject.util.CurrencyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class CurrentAccountMapper {
    private final CurrencyUtil currencyUtil;

    public CurrentAccountResponse toResponse(CurrentAccountEntity currentAccountEntity) {
        CurrentAccountResponse currentAccountResponse = new CurrentAccountResponse();
        currentAccountResponse.setId(currentAccountEntity.getId());
        currentAccountResponse.setNumber(currentAccountEntity.getNumber());
        currentAccountResponse.setCurrency(currentAccountEntity.getCurrentCurrency());
        currentAccountResponse.setExpirationDate(currentAccountEntity.getExpirationDate());
        currentAccountResponse.setCustomerId(currentAccountEntity.getCustomer().getId());
        currentAccountResponse.setBalance(
                currentAccountEntity.getBalance()
                        .multiply(currencyUtil.getExchangeRate(currentAccountEntity.getBaseCurrency(), currentAccountEntity.getCurrentCurrency()))
                        .setScale(2, RoundingMode.HALF_UP)
        );
        return currentAccountResponse;
    }
}
