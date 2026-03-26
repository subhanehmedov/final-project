package com.demo.subhanfinalproject.mapper;

import com.demo.subhanfinalproject.model.dto.response.CardResponse;
import com.demo.subhanfinalproject.model.entity.CardEntity;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {
    public CardResponse toResponse(CardEntity cardEntity) {
        CardResponse cardResponse = new CardResponse();
        cardResponse.setNumber(cardEntity.getNumber());
        cardResponse.setCvv(cardEntity.getCvv());
        cardResponse.setBalance(cardEntity.getBalance());
        cardResponse.setExpirationDate(cardEntity.getExpirationDate());
        cardResponse.setCustomerId(cardEntity.getId());
        return cardResponse;
    }
}
