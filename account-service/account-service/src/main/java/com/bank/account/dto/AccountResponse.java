package com.bank.account.dto;

import java.math.BigDecimal;

public class AccountResponse {
    private String accountNumber;
    private String accountType;
    private BigDecimal balance;
    private String currency;
    private String message;

    public AccountResponse(String accountNumber, String accountType, BigDecimal balance, String currency, String message) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.message = message;
    }

    public String getAccountNumber() { return accountNumber; }
    public String getAccountType() { return accountType; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
    public String getMessage() { return message; }
}