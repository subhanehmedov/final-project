package com.demo.subhanfinalproject.repository;

import com.demo.subhanfinalproject.model.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
