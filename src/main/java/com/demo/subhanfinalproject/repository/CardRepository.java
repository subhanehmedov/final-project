package com.demo.subhanfinalproject.repository;

import com.demo.subhanfinalproject.model.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardRepository extends JpaRepository<CardEntity, Long> {
    Optional<CardEntity> findByNumber(String number);
}
