package com.demo.subhanfinalproject.mapper;

import com.demo.subhanfinalproject.model.dto.response.CardResponse;
import com.demo.subhanfinalproject.model.entity.CardEntity;
import com.demo.subhanfinalproject.util.CurrencyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;

@Component
@RequiredArgsConstructor
public class CardMapper {
    public final CurrencyUtil currencyUtil;

    public CardResponse toResponse(CardEntity cardEntity) {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setCurrency(cardEntity.getCurrentCurrency());
        cardResponse.setNumber(cardEntity.getNumber());
        cardResponse.setCvv(cardEntity.getCvv());
        cardResponse.setExpirationDate(cardEntity.getExpirationDate());
        cardResponse.setCustomerId(cardEntity.getCustomer().getId());
        cardResponse.setBalance(
                cardEntity.getBalance()
                        .multiply(currencyUtil.getExchangeRate(cardEntity.getBaseCurrency(), cardEntity.getCurrentCurrency()))
                        .setScale(2, RoundingMode.HALF_UP)
        );
        cardResponse.setCardStatus(cardEntity.getStatus());
        return cardResponse;
    }
}
