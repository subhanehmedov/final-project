package com.demo.subhanfinalproject.repository;

import com.demo.subhanfinalproject.model.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<CardEntity, Long> {
}
