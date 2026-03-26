package com.demo.subhanfinalproject.util;

import com.demo.subhanfinalproject.model.enums.Currency;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CurrencyUtil {
    public BigDecimal getExchangeRate(Currency from, Currency to) {
        if (from == Currency.AZN && to == Currency.USD) return new BigDecimal("0.59");
        if (from == Currency.AZN && to == Currency.EUR) return new BigDecimal("0.51");
        if (from == Currency.USD && to == Currency.AZN) return new BigDecimal("1.70");
        if (from == Currency.USD && to == Currency.EUR) return new BigDecimal("0.87");
        if (from == Currency.EUR && to == Currency.USD) return new BigDecimal("1.16");
        if (from == Currency.EUR && to == Currency.AZN) return new BigDecimal("1.95");

        return BigDecimal.ONE;
    }
}
