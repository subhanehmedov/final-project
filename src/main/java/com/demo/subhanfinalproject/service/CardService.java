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

    public CardResponse orderCard(CardRequest cardRequest, Long customerId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        if (customerEntity.getCardCount() >= 2) {
            throw new LimitExceededException("Max card count must be 2");
        }
        CardEntity cardEntity = initializeCard();
        cardEntity.setBalance(BigDecimal.ZERO);
        cardEntity.setCurrency(cardRequest.getCurrency());
        cardEntity.setExpirationDate(LocalDateTime.now().plusYears(3));
        cardEntity.setCustomer(customerEntity);
        customerEntity.setCardCount(customerEntity.getCardCount() + 1);
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
        Currency sourceCurrency = cardEntity.getCurrency();
        Currency targetCurrency = cardRequest.getCurrency();

        if (!sourceCurrency.equals(targetCurrency)) {
            BigDecimal rate = getExchangeRate(sourceCurrency, targetCurrency);
            BigDecimal newBalance = cardEntity.getBalance()
                    .multiply(rate)
                    .setScale(2, RoundingMode.HALF_UP);
            cardEntity.setBalance(newBalance);
            cardEntity.setCurrency(targetCurrency);
        }
        cardRepository.save(cardEntity);
        return cardMapper.toResponse(cardEntity);
    }

    public void deleteCard(Long id) {
        CardEntity cardEntity = cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
        cardEntity.getCustomer().setCardCount(cardEntity.getCustomer().getCardCount() - 1);
        cardRepository.delete(cardEntity);
    }

    private BigDecimal getExchangeRate(Currency from, Currency to) {
        if (from == Currency.AZN && to == Currency.USD) return new BigDecimal("0.59");
        if (from == Currency.AZN && to == Currency.EUR) return new BigDecimal("0.51");
        if (from == Currency.USD && to == Currency.AZN) return new BigDecimal("1.70");
        if (from == Currency.USD && to == Currency.EUR) return new BigDecimal("0.87");
        if (from == Currency.EUR && to == Currency.USD) return new BigDecimal("1.16");
        if (from == Currency.EUR && to == Currency.AZN) return new BigDecimal("1.95");

        return BigDecimal.ONE;
    }
}
