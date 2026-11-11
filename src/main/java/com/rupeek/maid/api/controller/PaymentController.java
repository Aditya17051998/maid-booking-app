package com.rupeek.maid.api.controller;

import com.rupeek.maid.api.dto.PaymentResponse;
import com.rupeek.maid.api.dto.ProcessPaymentRequest;
import com.rupeek.maid.api.mapper.PaymentMapper;
import com.rupeek.maid.domain.model.Payment;
import com.rupeek.maid.domain.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "APIs for payment processing")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentMapper paymentMapper;

    @PostMapping
    @Operation(summary = "Process payment", description = "Process payment for a booking")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        Payment payment = paymentService.processPayment(
            request.getBookingId(),
            request.getPaymentMethod(),
            request.getAmount(),
            request.getIdempotencyKey()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentMapper.toResponse(payment));
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment", description = "Refund a completed payment")
    public ResponseEntity<PaymentResponse> refundPayment(@PathVariable Long id) {
        Payment payment = paymentService.refundPayment(id);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }

    @GetMapping("/booking/{bookingId}")
    @Operation(summary = "Get booking payments", description = "Retrieve all payments for a booking")
    public ResponseEntity<List<PaymentResponse>> getBookingPayments(@PathVariable Long bookingId) {
        List<Payment> payments = paymentService.getBookingPayments(bookingId);
        List<PaymentResponse> responses = payments.stream()
            .map(paymentMapper::toResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", description = "Retrieve payment by transaction ID")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(@PathVariable String transactionId) {
        Payment payment = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(paymentMapper.toResponse(payment));
    }
}
