package com.bank.account.dto;

import java.math.BigDecimal;

public class BalanceResponse {
    private String accountNumber;
    private BigDecimal balance;
    private String currency;

    public BalanceResponse(String accountNumber, BigDecimal balance, String currency) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
    }

    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public String getCurrency() { return currency; }
}