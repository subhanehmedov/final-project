package com.demo.subhanfinalproject.mapper;

import com.demo.subhanfinalproject.model.dto.response.CustomerRequest;
import com.demo.subhanfinalproject.model.dto.response.CustomerResponse;
import com.demo.subhanfinalproject.model.entity.CustomerEntity;
import com.demo.subhanfinalproject.model.enums.CustomerStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerMapper {
    private final CardMapper cardMapper;
    private final CurrentAccountMapper currentAccountMapper;

    public CustomerEntity toEntity(CustomerRequest customerRequest) {
        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setName(customerRequest.getName());
        customerEntity.setSurname(customerRequest.getSurname());
        customerEntity.setStatus(CustomerStatus.PENDING);
        return customerEntity;
    }

    public CustomerResponse toResponse(CustomerEntity customerEntity) {
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setId(customerEntity.getId());
        customerResponse.setFirstName(customerEntity.getName());
        customerResponse.setLastName(customerEntity.getSurname());
        customerResponse.setStatus(customerEntity.getStatus());
        customerResponse.setCards(customerEntity.getCards().stream()
                .map(cardMapper::toResponse)
                .toList());
        customerResponse.setCurrentAccounts(customerEntity.getCurrentAccounts().stream()
                .map(currentAccountMapper::toResponse)
                .toList());
        return customerResponse;
    }
}
