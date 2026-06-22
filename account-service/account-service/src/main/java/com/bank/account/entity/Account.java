package com.bank.account.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String accountType;

    @Column(nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    private String currency = "USD";
    private LocalDateTime createdAt;
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (accountNumber == null) {
            accountNumber = "ACC" + System.currentTimeMillis();
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getUsername() { return username; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Boolean getIsActive() { return isActive; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public void setUsername(String username) { this.username = username; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}