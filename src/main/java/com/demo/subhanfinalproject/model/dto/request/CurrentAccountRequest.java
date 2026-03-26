package com.demo.subhanfinalproject.model.dto.request;

import com.demo.subhanfinalproject.model.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CurrentAccountRequest {
    private Currency currency;
}
