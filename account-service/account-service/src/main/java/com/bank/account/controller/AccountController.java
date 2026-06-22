package com.bank.account.controller;

import com.bank.account.dto.AccountResponse;
import com.bank.account.dto.BalanceResponse;
import com.bank.account.dto.CreateAccountRequest;
import com.bank.account.entity.Account;
import com.bank.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setAccountType(request.getAccountType() != null ? request.getAccountType() : "SAVINGS");
        account.setBalance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO);
        account.setCurrency("USD");
        account.setIsActive(true);

        Account saved = accountRepository.save(account);

        AccountResponse response = new AccountResponse(
                saved.getAccountNumber(),
                saved.getAccountType(),
                saved.getBalance(),
                saved.getCurrency(),
                "Account created successfully"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<Account>> getUserAccounts(@PathVariable String username) {
        return ResponseEntity.ok(accountRepository.findByUsername(username));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<?> getAccount(@PathVariable String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found"));
        }
        return ResponseEntity.ok(account);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<?> getBalance(@PathVariable String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found"));
        }
        return ResponseEntity.ok(new BalanceResponse(account.getAccountNumber(), account.getBalance(), account.getCurrency()));
    }

    @PutMapping("/{accountNumber}/balance")
    public ResponseEntity<?> updateBalance(@PathVariable String accountNumber,
                                           @RequestBody Map<String, BigDecimal> request) {
        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Account not found"));
        }
        account.setBalance(request.get("balance"));
        accountRepository.save(account);
        return ResponseEntity.ok(Map.of("message", "Balance updated", "newBalance", request.get("balance")));
    }
}