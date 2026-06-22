package com.bank.transaction.dto;

import java.math.BigDecimal;

public class TransferResponse {
    private String transactionId;
    private String message;
    private BigDecimal newBalance;

    public TransferResponse(String transactionId, String message, BigDecimal newBalance) {
        this.transactionId = transactionId;
        this.message = message;
        this.newBalance = newBalance;
    }

    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
    public BigDecimal getNewBalance() { return newBalance; }
}