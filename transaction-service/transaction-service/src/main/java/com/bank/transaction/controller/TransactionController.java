package com.bank.transaction.controller;

import com.bank.transaction.dto.TransferRequest;
import com.bank.transaction.dto.TransferResponse;
import com.bank.transaction.entity.Transaction;
import com.bank.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    private RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@RequestBody TransferRequest request) {
        try {
            // 1. Get source account balance
            String balanceUrl = "http://localhost:8082/api/accounts/" + request.getFromAccount() + "/balance";
            ResponseEntity<Map> balanceResponse;
            try {
                balanceResponse = restTemplate.getForEntity(balanceUrl, Map.class);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Source account not found"));
            }

            BigDecimal sourceBalance = new BigDecimal(balanceResponse.getBody().get("balance").toString());

            // 2. Check sufficient balance
            if (sourceBalance.compareTo(request.getAmount()) < 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Insufficient balance"));
            }

            // 3. Deduct from source account
            BigDecimal newSourceBalance = sourceBalance.subtract(request.getAmount());
            Map<String, BigDecimal> updateMap = new HashMap<>();
            updateMap.put("balance", newSourceBalance);
            String accountUrl = "http://localhost:8082/api/accounts/" + request.getFromAccount();
            restTemplate.put(accountUrl + "/balance", updateMap);

            // 4. Add to destination account
            String destUrl = "http://localhost:8082/api/accounts/" + request.getToAccount();
            ResponseEntity<Map> destResponse = restTemplate.getForEntity(destUrl, Map.class);
            BigDecimal destBalance = new BigDecimal(destResponse.getBody().get("balance").toString());
            BigDecimal newDestBalance = destBalance.add(request.getAmount());
            updateMap.put("balance", newDestBalance);
            restTemplate.put(destUrl + "/balance", updateMap);

            // 5. Create transaction record
            Transaction transaction = new Transaction();
            transaction.setFromAccount(request.getFromAccount());
            transaction.setToAccount(request.getToAccount());
            transaction.setAmount(request.getAmount());
            transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Fund Transfer");
            transaction.setUsername(request.getUsername());
            transaction.setStatus("COMPLETED");

            Transaction saved = transactionRepository.save(transaction);

            return ResponseEntity.ok(new TransferResponse(
                    saved.getTransactionId(),
                    "Transfer successful",
                    newSourceBalance
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Transfer failed: " + e.getMessage()));
        }
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String username) {
        return ResponseEntity.ok(transactionRepository.findByUsernameOrderByTimestampDesc(username));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<?> getTransaction(@PathVariable String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId).orElse(null);
        if (transaction == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Transaction not found"));
        }
        return ResponseEntity.ok(transaction);
    }
}