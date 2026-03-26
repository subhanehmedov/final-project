package com.demo.subhanfinalproject.service;

import com.demo.subhanfinalproject.exception.custom.LimitExceededException;
import com.demo.subhanfinalproject.exception.custom.NotFoundException;
import com.demo.subhanfinalproject.mapper.CardMapper;
import com.demo.subhanfinalproject.model.dto.request.CardRequest;
import com.demo.subhanfinalproject.model.dto.response.CardResponse;
import com.demo.subhanfinalproject.model.entity.CardEntity;
import com.demo.subhanfinalproject.model.entity.CustomerEntity;
import com.demo.subhanfinalproject.model.enums.Currency;
import com.demo.subhanfinalproject.repository.CardRepository;
import com.demo.subhanfinalproject.repository.CustomerRepository;
import com.demo.subhanfinalproject.util.CurrencyUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CustomerRepository customerRepository;
    private final CardMapper cardMapper;
    private final CardRepository cardRepository;
    private final CurrencyUtil currencyUtil;

    public CardResponse orderCard(CardRequest cardRequest, Long customerId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        if (customerEntity.getCards().size() >= 2) {
            throw new LimitExceededException("Max card count must be 2");
        }
        CardEntity cardEntity = initializeCard();
        cardEntity.setBalance(BigDecimal.ZERO);
        cardEntity.setCurrentCurrency(cardRequest.getCurrency());
        cardEntity.setBaseCurrency(cardRequest.getCurrency());
        cardEntity.setExpirationDate(LocalDateTime.now().plusYears(3));
        cardEntity.setCustomer(customerEntity);
        CardEntity savedEntity = cardRepository.save(cardEntity);
        return cardMapper.toResponse(savedEntity);
    }

    private CardEntity initializeCard() {
        CardEntity cardEntity = new CardEntity();
        StringBuilder cardNumber = new StringBuilder("41697388");
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            cardNumber.append(random.nextInt(10));
        }
        StringBuilder cvv = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvv.append(random.nextInt(10));
        }
        cardEntity.setNumber(cardNumber.toString());
        cardEntity.setCvv(cvv.toString());
        return cardEntity;
    }

    public List<CardResponse> getAllCards() {
        return cardRepository.findAll().stream()
                .map(cardMapper::toResponse)
                .toList();
    }

    public CardResponse getCardById(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        return cardMapper.toResponse(cardEntity);
    }

    public CardResponse updateCard(Long id, CardRequest cardRequest) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        cardEntity.setCurrentCurrency(cardRequest.getCurrency());
        cardRepository.save(cardEntity);
        return cardMapper.toResponse(cardEntity);
    }

    public void deleteCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        cardRepository.delete(cardEntity);
    }
}
