package com.demo.subhanfinalproject.repository;

import com.demo.subhanfinalproject.model.entity.CurrentAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrentAccountRepository extends JpaRepository<CurrentAccountEntity, Long> {
    Optional<CurrentAccountEntity> findByNumber(String number);
}
