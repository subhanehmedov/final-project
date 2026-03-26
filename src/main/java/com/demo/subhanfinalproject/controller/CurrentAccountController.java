package com.demo.subhanfinalproject.controller;

import com.demo.subhanfinalproject.model.dto.request.CurrentAccountRequest;
import com.demo.subhanfinalproject.model.dto.response.CurrentAccountResponse;
import com.demo.subhanfinalproject.service.CurrentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/current-accounts")
public class CurrentAccountController {
    private final CurrentAccountService currentAccountService;

    @PostMapping("/{customerId}")
    public ResponseEntity<CurrentAccountResponse> createCurrentAccount(
            @RequestBody CurrentAccountRequest currentAccountRequest,
            @PathVariable Long customerId
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(currentAccountService.createCurrentAccount(currentAccountRequest, customerId));
    }

    @GetMapping
    public ResponseEntity<List<CurrentAccountResponse>> getAllCurrentAccounts() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(currentAccountService.getAllCurrentAccounts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CurrentAccountResponse> getCurrentAccountById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(currentAccountService.getCurrentAccountById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CurrentAccountResponse> updateCurrentAccount(
            @RequestBody CurrentAccountRequest currentAccountRequest,
            @PathVariable Long id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(currentAccountService.updateCurrentAccount(currentAccountRequest, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCurrentAccountById(@PathVariable Long id) {
        currentAccountService.deleteCurrentAccount(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
