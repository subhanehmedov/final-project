package com.demo.subhanfinalproject.service;

import com.demo.subhanfinalproject.exception.custom.NotFoundException;
import com.demo.subhanfinalproject.mapper.CustomerMapper;
import com.demo.subhanfinalproject.model.dto.request.CustomerRequest;
import com.demo.subhanfinalproject.model.dto.response.CustomerResponse;
import com.demo.subhanfinalproject.model.entity.CustomerEntity;
import com.demo.subhanfinalproject.model.enums.CustomerStatus;
import com.demo.subhanfinalproject.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerMapper customerMapper;
    private final CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CustomerRequest customerRequest) {
        CustomerEntity customerEntity = customerMapper.toEntity(customerRequest);
        customerEntity.setStatus(CustomerStatus.ACTIVE);
        CustomerEntity savedEntity = customerRepository.save(customerEntity);
        return customerMapper.toResponse(savedEntity);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(customerMapper::toResponse)
                .toList();
    }

    public CustomerResponse getCustomerById(Long id) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        return customerMapper.toResponse(customerEntity);
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest customerRequest) {
        CustomerEntity customerEntity = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        customerEntity.setName(customerRequest.getName());
        customerEntity.setSurname(customerRequest.getSurname());
        CustomerEntity savedEntity = customerRepository.save(customerEntity);
        return customerMapper.toResponse(savedEntity);
    }

    public void deleteCustomer(Long id) {
        CustomerEntity customerEntity =  customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        customerRepository.delete(customerEntity);
    }
}
