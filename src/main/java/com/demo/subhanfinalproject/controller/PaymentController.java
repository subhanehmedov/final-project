package com.demo.subhanfinalproject.controller;

import com.demo.subhanfinalproject.model.dto.request.PaymentRequest;
import com.demo.subhanfinalproject.model.dto.response.PaymentResponse;
import com.demo.subhanfinalproject.model.enums.PaymentMethod;
import com.demo.subhanfinalproject.model.enums.PaymentReportType;
import com.demo.subhanfinalproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> pay(
            @RequestParam PaymentMethod paymentMethod,
            @RequestBody PaymentRequest paymentRequest
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.pay(paymentMethod, paymentRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.getPaymentById(id));
    }

    @GetMapping("/report/{cardOrCurrentAccountId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentReport(
            @RequestParam PaymentReportType paymentReportType,
            @PathVariable Long cardOrCurrentAccountId
            ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(paymentService.getPaymentReport(paymentReportType, cardOrCurrentAccountId));
    }
}
