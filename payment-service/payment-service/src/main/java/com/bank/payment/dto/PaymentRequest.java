package com.bank.payment.dto;

import java.math.BigDecimal;

public class PaymentRequest {
    private String fromAccount;
    private String billType;
    private String billerName;
    private BigDecimal amount;
    private String username;
    private String description;

    public String getFromAccount() { return fromAccount; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }

    public String getBillType() { return billType; }
    public void setBillType(String billType) { this.billType = billType; }

    public String getBillerName() { return billerName; }
    public void setBillerName(String billerName) { this.billerName = billerName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}