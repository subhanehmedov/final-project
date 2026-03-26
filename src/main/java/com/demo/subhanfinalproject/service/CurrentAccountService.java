package com.demo.subhanfinalproject.service;

import com.demo.subhanfinalproject.exception.custom.LimitExceededException;
import com.demo.subhanfinalproject.exception.custom.NotFoundException;
import com.demo.subhanfinalproject.mapper.CurrentAccountMapper;
import com.demo.subhanfinalproject.model.dto.request.CurrentAccountRequest;
import com.demo.subhanfinalproject.model.dto.response.CurrentAccountResponse;
import com.demo.subhanfinalproject.model.entity.CurrentAccountEntity;
import com.demo.subhanfinalproject.model.entity.CustomerEntity;
import com.demo.subhanfinalproject.repository.CurrentAccountRepository;
import com.demo.subhanfinalproject.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CurrentAccountService {
    private final CustomerRepository customerRepository;
    private final CurrentAccountRepository currentAccountRepository;
    private final CurrentAccountMapper currentAccountMapper;

    public CurrentAccountResponse createCurrentAccount(CurrentAccountRequest currentAccountRequest, Long customerId) {
        CustomerEntity customerEntity = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        if (customerEntity.getCurrentAccounts().size() >= 3) {
            throw new LimitExceededException("Max current account count must be 2");
        }
        CurrentAccountEntity currentAccountEntity = initializeCurrentAccount();
        currentAccountEntity.setCustomer(customerEntity);
        currentAccountEntity.setCurrentCurrency(currentAccountRequest.getCurrency());
        currentAccountEntity.setBaseCurrency(currentAccountRequest.getCurrency());
        currentAccountEntity.setBalance(BigDecimal.ZERO);
        currentAccountEntity.setExpirationDate(LocalDateTime.now().plusYears(3));
        CurrentAccountEntity savedEntity = currentAccountRepository.save(currentAccountEntity);
        return currentAccountMapper.toResponse(savedEntity);
    }

    public List<CurrentAccountResponse> getAllCurrentAccounts() {
        return currentAccountRepository.findAll().stream()
                .map(currentAccountMapper::toResponse)
                .toList();
    }

    public CurrentAccountResponse getCurrentAccountById(Long id) {
        CurrentAccountEntity currentAccountEntity = currentAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Current Account not found"));
        return currentAccountMapper.toResponse(currentAccountEntity);
    }

    public CurrentAccountResponse updateCurrentAccount(CurrentAccountRequest currentAccountRequest, Long id) {
        CurrentAccountEntity currentAccountEntity = currentAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Current Account not found"));
        currentAccountEntity.setCurrentCurrency(currentAccountRequest.getCurrency());
        currentAccountRepository.save(currentAccountEntity);
        return currentAccountMapper.toResponse(currentAccountEntity);
    }

    public void deleteCurrentAccount(Long id) {
        CurrentAccountEntity entity = currentAccountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Current Account not found"));
        currentAccountRepository.delete(entity);
    }

    private CurrentAccountEntity initializeCurrentAccount() {
        CurrentAccountEntity currentAccountEntity = new CurrentAccountEntity();

        StringBuilder currentAccountNumber = new StringBuilder("410100L944193712");
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            currentAccountNumber.append(random.nextInt(10));
        }
        currentAccountEntity.setNumber(currentAccountNumber.toString());
        return currentAccountEntity;
    }
}
