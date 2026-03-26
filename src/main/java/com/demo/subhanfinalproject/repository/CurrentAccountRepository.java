package com.demo.subhanfinalproject.repository;

import com.demo.subhanfinalproject.model.entity.CurrentAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentAccountRepository extends JpaRepository<CurrentAccountEntity, Long> {
}
