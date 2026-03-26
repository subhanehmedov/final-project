package com.demo.subhanfinalproject.service;

import com.demo.subhanfinalproject.exception.custom.CardIsNotActiveException;
import com.demo.subhanfinalproject.exception.custom.InsufficientBalanceException;
import com.demo.subhanfinalproject.exception.custom.LimitExceededException;
import com.demo.subhanfinalproject.exception.custom.NotFoundException;
import com.demo.subhanfinalproject.mapper.PaymentMapper;
import com.demo.subhanfinalproject.model.dto.request.PaymentRequest;
import com.demo.subhanfinalproject.model.dto.response.PaymentResponse;
import com.demo.subhanfinalproject.model.entity.CardEntity;
import com.demo.subhanfinalproject.model.entity.CurrentAccountEntity;
import com.demo.subhanfinalproject.model.entity.CustomerEntity;
import com.demo.subhanfinalproject.model.entity.PaymentEntity;
import com.demo.subhanfinalproject.model.enums.*;
import com.demo.subhanfinalproject.repository.CardRepository;
import com.demo.subhanfinalproject.repository.CurrentAccountRepository;
import com.demo.subhanfinalproject.repository.CustomerRepository;
import com.demo.subhanfinalproject.repository.PaymentRepository;
import com.demo.subhanfinalproject.util.CurrencyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {
    private final CardRepository cardRepository;
    private final CurrencyUtil currencyUtil;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CurrentAccountRepository currentAccountRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = CardIsNotActiveException.class)
    public PaymentResponse pay(PaymentMethod paymentMethod, PaymentRequest paymentRequest) {
        Object from = getSender(paymentMethod, paymentRequest.getFrom());
        Object to = getReceiver(paymentMethod, paymentRequest.getTo());

        Long customerId = (from instanceof CardEntity card)
                ? card.getCustomer().getId()
                : ((CurrentAccountEntity) from).getCustomer().getId();

        boolean isFromCard = (from instanceof CardEntity);

        checkDailyConflict(customerId, isFromCard);

        validatePotentialBalance(from, to, paymentRequest.getAmount());

        PaymentEntity paymentEntity = new PaymentEntity();
        setupPaymentEntity(paymentEntity, from, to, paymentRequest);
        paymentEntity.setPaymentStatus(PaymentStatus.NEW);

        if (from instanceof CardEntity cardEntity) {
            paymentEntity.setCustomer(cardEntity.getCustomer());
        } else {
            CurrentAccountEntity currentAccountEntity = (CurrentAccountEntity) from;
            paymentEntity.setCustomer(currentAccountEntity.getCustomer());
        }
        paymentRepository.save(paymentEntity);
        return paymentMapper.toResponse(paymentEntity);
    }

    public PaymentResponse getPaymentById(Long id) {
        PaymentEntity paymentEntity = paymentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Payment not found"));
        return paymentMapper.toResponse(paymentEntity);
    }


    private void validatePotentialBalance(Object from, Object to, BigDecimal amount) {
        BigDecimal fromBalance = getBalance(from);
        Currency fromBaseCurrency = getBaseCurrency(from);

        BigDecimal requiredMin = (from instanceof CardEntity)
                ? currencyUtil.convert(BigDecimal.valueOf(10), Currency.AZN, fromBaseCurrency)
                : currencyUtil.convert(BigDecimal.valueOf(5), Currency.USD, fromBaseCurrency);

        if (fromBalance.compareTo(requiredMin) < 0) {
            throw new InsufficientBalanceException("Minimum balance requirement not met");
        }

        if (fromBalance.compareTo(amount) < 0) {
            if (from instanceof CardEntity card) {
                boolean hasAlternative = findAlternativeAccount(card, to, amount, fromBaseCurrency).isPresent();
                if (!hasAlternative) {
                    throw new InsufficientBalanceException("Insufficient funds on card and all linked active accounts");
                }
            } else {
                throw new InsufficientBalanceException("Insufficient balance on current account");
            }
        }
    }

    private Object executeTransfer(Object from, Object to, BigDecimal amount) {
        BigDecimal fromBalance = getBalance(from);
        Currency fromBaseCurrency = getBaseCurrency(from);
        Currency toBaseCurrency = getBaseCurrency(to);

        BigDecimal requiredMinBalance = (from instanceof CardEntity)
                ? currencyUtil.convert(BigDecimal.valueOf(10), Currency.AZN, fromBaseCurrency)
                : currencyUtil.convert(BigDecimal.valueOf(5), Currency.USD, fromBaseCurrency);

        if (fromBalance.compareTo(requiredMinBalance) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        Object actualSender = from;
        BigDecimal actualAmount = amount;
        BigDecimal actualBalance = fromBalance;

        if (fromBalance.compareTo(amount) < 0) {
            if (from instanceof CardEntity cardEntity) {
                actualSender = findAlternativeAccount(cardEntity, to, amount, fromBaseCurrency)
                        .orElseThrow(() -> new InsufficientBalanceException("No active alternative account with sufficient balance"));

                actualBalance = getBalance(actualSender);
                Currency actualSenderBaseCurrency = getBaseCurrency(actualSender);
                actualAmount = currencyUtil.convert(amount, fromBaseCurrency, actualSenderBaseCurrency);
            } else {
                throw new InsufficientBalanceException("Insufficient balance on current account");
            }
        }

        updateBalance(actualSender, actualBalance.subtract(actualAmount));

        BigDecimal finalConvertedAmount = currencyUtil.convert(actualAmount, getBaseCurrency(actualSender), toBaseCurrency);
        updateBalance(to, getBalance(to).add(finalConvertedAmount));

        return actualSender;
    }

    private Optional<CurrentAccountEntity> findAlternativeAccount(CardEntity card, Object to, BigDecimal originalAmount, Currency cardCurrency) {
        List<CurrentAccountEntity> customerAccounts = card.getCustomer().getCurrentAccounts();
        Long targetAccountId = (to instanceof CurrentAccountEntity currentAccount) ? currentAccount.getId() : null;

        return customerAccounts.stream()
                .filter(acc -> !Objects.equals(acc.getId(), targetAccountId))
                .filter(acc -> acc.getStatus() == CurrentAccountStatus.ACTIVE)
                .filter(acc -> {
                    BigDecimal convertedAmount = currencyUtil.convert(originalAmount, cardCurrency, acc.getBaseCurrency());
                    return acc.getBalance().compareTo(convertedAmount) >= 0;
                })
                .findFirst();
    }

    @Transactional
    public void processPendingPayments() {
        List<PaymentEntity> newPayments = paymentRepository.findAllByPaymentStatus(PaymentStatus.NEW);

        for (PaymentEntity payment : newPayments) {
            try {
                Object originalFrom = (payment.getFromCard() != null) ? payment.getFromCard() : payment.getFromCurrentAccount();
                Object to = (payment.getToCard() != null) ? payment.getToCard() : payment.getToCurrentAccount();

                Object actualSender = executeTransfer(originalFrom, to, payment.getAmount());

                if (actualSender instanceof CurrentAccountEntity alternativeAcc && payment.getFromCard() != null) {
                    payment.setFromCard(null);
                    payment.setFromCurrentAccount(alternativeAcc);
                }

                payment.setPaymentStatus(PaymentStatus.SUCCESS);
                payment.setProcessedAt(LocalDateTime.now());

            } catch (Exception e) {
                payment.setPaymentStatus(PaymentStatus.FAILED);
            } finally {
                paymentRepository.save(payment);
            }
        }
        checkCustomerMonthlyLimit();
    }

    private BigDecimal getBalance(Object account) {
        if (account instanceof CardEntity) {
            return ((CardEntity) account).getBalance();
        }
        return ((CurrentAccountEntity) account).getBalance();
    }

    private void updateBalance(Object account, BigDecimal newBalance) {
        if (account instanceof CardEntity) {
            ((CardEntity) account).setBalance(newBalance);
        } else ((CurrentAccountEntity) account).setBalance(newBalance);
    }

    private Currency getBaseCurrency(Object account) {
        if (account instanceof CardEntity) {
            return ((CardEntity) account).getBaseCurrency();
        }
        return ((CurrentAccountEntity) account).getBaseCurrency();
    }

    private Object getSender(PaymentMethod method, String number) {
        if (method.name().startsWith("FROM_CARD")) {
            CardEntity cardEntity = cardRepository.findByNumber(number)
                    .orElseThrow(() -> new NotFoundException("Card not found"));
            isPaymentEligible(cardEntity);
            return cardEntity;
        } else {
            CurrentAccountEntity currentAccountEntity = currentAccountRepository.findByNumber(number)
                    .orElseThrow(() -> new NotFoundException("Current account not found"));
            isPaymentEligible(currentAccountEntity);
            return currentAccountEntity;
        }
    }

    private Object getReceiver(PaymentMethod method, String number) {
        if (method.name().endsWith("TO_CARD")) {
            CardEntity cardEntity = cardRepository.findByNumber(number)
                    .orElseThrow(() -> new NotFoundException("Card not found"));
            isPaymentEligible(cardEntity);
            return cardEntity;
        } else {
            CurrentAccountEntity currentAccountEntity = currentAccountRepository.findByNumber(number)
                    .orElseThrow(() -> new NotFoundException("Current account not found"));
            isPaymentEligible(currentAccountEntity);
            return currentAccountEntity;
        }
    }

    private void isPaymentEligible(Object obj) {
        if (obj instanceof CardEntity cardEntity) {
            if (cardEntity.getExpirationDate().isBefore(LocalDateTime.now())) {
                cardEntity.setStatus(CardStatus.EXPIRED);
                cardRepository.save(cardEntity);
                throw new CardIsNotActiveException("Card expired");
            }
            if (cardEntity.getStatus().equals(CardStatus.EXPIRED) ||
                    cardEntity.getStatus().equals(CardStatus.PROCESS)) {
                throw new CardIsNotActiveException("Card is not active");
            }
        } else if (obj instanceof CurrentAccountEntity currentAccountEntity) {
            if (currentAccountEntity.getExpirationDate().isBefore(LocalDateTime.now())) {
                currentAccountEntity.setStatus(CurrentAccountStatus.EXPIRED);
                currentAccountRepository.save(currentAccountEntity);
                throw new CardIsNotActiveException("Card expired");
            }
            if (currentAccountEntity.getStatus().equals(CurrentAccountStatus.EXPIRED) ||
                    currentAccountEntity.getStatus().equals(CurrentAccountStatus.PROCESS)) {
                throw new CardIsNotActiveException("Current account is not active");
            }
        }
    }

    private void setupPaymentEntity(PaymentEntity entity, Object from, Object to, PaymentRequest request) {
        entity.setCurrency(getBaseCurrency(from));
        entity.setAmount(request.getAmount());
        entity.setProcessedAt(LocalDateTime.now());
        entity.setPaymentStatus(PaymentStatus.NEW);

        if (from instanceof CardEntity) {
            entity.setFromCard((CardEntity) from);
        } else {
            entity.setFromCurrentAccount((CurrentAccountEntity) from);
        }

        if (to instanceof CardEntity) {
            entity.setToCard((CardEntity) to);
        } else {
            entity.setToCurrentAccount((CurrentAccountEntity) to);
        }
    }

    private void checkDailyConflict(Long customerId, boolean isFromCard) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();

        if (isFromCard) {
            boolean existsAccountPayment = paymentRepository.existsByCustomerIdAndFromCurrentAccountIsNotNullAndCreatedAtAfter(customerId, startOfDay);
            if (existsAccountPayment) {
                throw new LimitExceededException("You have already made a payment from your account today; you cannot use a card");
            }
        } else {
            boolean existsCardPayment = paymentRepository.existsByCustomerIdAndFromCardIsNotNullAndCreatedAtAfter(customerId, startOfDay);
            if (existsCardPayment) {
                throw new LimitExceededException("You have already made a payment from your card today; you cannot use a current account");
            }
        }
    }

    public void checkCustomerMonthlyLimit() {
        log.info("checkCustomerMonthlyLimit started");
        List<CustomerEntity> customers = customerRepository.findAll();

        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        for (CustomerEntity customerEntity : customers) {
            List<PaymentEntity> paymentEntities = paymentRepository.findAllByCustomerIdAndPaymentStatusAndProcessedAtAfter(
                    customerEntity.getId(), PaymentStatus.SUCCESS, oneMonthAgo
            );
            BigDecimal sum = BigDecimal.ZERO;
            for (PaymentEntity paymentEntity : paymentEntities) {
                BigDecimal amountInAzn = currencyUtil.convert(paymentEntity.getAmount(), paymentEntity.getCurrency(), Currency.AZN);
                sum = sum.add(amountInAzn);
            }
            if (sum.compareTo(BigDecimal.valueOf(100)) > 0) {
                customerEntity.setStatus(CustomerStatus.SUSPICIOUS);
                customerRepository.save(customerEntity);
                log.info("status suspicious");
            }
        }
        log.info("checkCustomerMonthlyLimit finished");
    }

    public List<PaymentResponse> getPaymentReport(PaymentReportType paymentReportType, Long id) {
        if (paymentReportType == PaymentReportType.CARD) {
            return paymentRepository.findTop100ByFromCardIdAndPaymentStatusOrderByProcessedAtDesc(
                            id, PaymentStatus.SUCCESS).stream()
                    .map(paymentMapper::toResponse)
                    .toList();
        } else {
            return paymentRepository.findTop100ByFromCurrentAccountIdAndPaymentStatusOrderByProcessedAtDesc(
                            id, PaymentStatus.SUCCESS).stream()
                    .map(paymentMapper::toResponse)
                    .toList();
        }
    }
}
