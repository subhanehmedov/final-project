package com.demo.subhanfinalproject.controller;

import com.demo.subhanfinalproject.model.dto.request.CardRequest;
import com.demo.subhanfinalproject.model.dto.response.CardResponse;
import com.demo.subhanfinalproject.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping("/{customerId}")
    public ResponseEntity<CardResponse> orderCard(
            @RequestBody CardRequest cardRequest,
            @PathVariable Long customerId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cardService.orderCard(cardRequest, customerId));
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardService.getAllCards());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardResponse> getCardById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardService.getCardById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CardResponse> updateCard(
            @PathVariable Long id,
            @RequestBody CardRequest cardRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(cardService.updateCard(id, cardRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
