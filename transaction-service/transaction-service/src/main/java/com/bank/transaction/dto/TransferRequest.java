package com.bank.transaction.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String description;
    private String username;

    public String getFromAccount() { return fromAccount; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }
    public String getToAccount() { return toAccount; }
    public void setToAccount(String toAccount) { this.toAccount = toAccount; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}