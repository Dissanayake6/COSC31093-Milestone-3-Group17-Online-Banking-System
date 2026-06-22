package com.bank.payment.controller;

import com.bank.payment.dto.PaymentRequest;
import com.bank.payment.dto.PaymentResponse;
import com.bank.payment.entity.Payment;
import com.bank.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/pay-bill")
    public ResponseEntity<?> payBill(@RequestBody PaymentRequest request) {
        try {
            // 1. Check if account exists and get balance
            String balanceUrl = "http://localhost:8082/api/accounts/" + request.getFromAccount() + "/balance";
            ResponseEntity<Map> balanceResponse;
            try {
                balanceResponse = restTemplate.getForEntity(balanceUrl, Map.class);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Account not found"));
            }

            BigDecimal currentBalance = new BigDecimal(balanceResponse.getBody().get("balance").toString());

            // 2. Check sufficient balance
            if (currentBalance.compareTo(request.getAmount()) < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Insufficient balance"));
            }

            // 3. Deduct amount from account
            BigDecimal newBalance = currentBalance.subtract(request.getAmount());
            Map<String, BigDecimal> updateMap = new HashMap<>();
            updateMap.put("balance", newBalance);
            String accountUrl = "http://localhost:8082/api/accounts/" + request.getFromAccount();
            restTemplate.put(accountUrl + "/balance", updateMap);

            // 4. Create payment record
            Payment payment = new Payment();
            payment.setFromAccount(request.getFromAccount());
            payment.setBillType(request.getBillType());
            payment.setBillerName(request.getBillerName());
            payment.setAmount(request.getAmount());
            payment.setUsername(request.getUsername());
            payment.setDescription(request.getDescription() != null ? request.getDescription() : "Bill Payment");
            payment.setStatus("COMPLETED");

            Payment saved = paymentRepository.save(payment);

            return ResponseEntity.ok(new PaymentResponse(
                    saved.getPaymentId(),
                    "Bill paid successfully",
                    newBalance
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Payment failed: " + e.getMessage()));
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<Payment>> getPaymentHistory(@PathVariable String username) {
        return ResponseEntity.ok(paymentRepository.findByUsernameOrderByPaymentDateDesc(username));
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<List<Payment>> getPaymentsByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(paymentRepository.findByFromAccount(accountNumber));
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPayment(@PathVariable String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId).orElse(null);
        if (payment == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Payment not found"));
        }
        return ResponseEntity.ok(payment);
    }
}