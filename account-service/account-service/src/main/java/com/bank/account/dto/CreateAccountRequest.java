package com.bank.account.dto;

import java.math.BigDecimal;

public class CreateAccountRequest {
    private String username;
    private String accountType;
    private BigDecimal initialDeposit;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
}