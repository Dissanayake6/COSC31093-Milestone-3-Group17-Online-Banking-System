package com.bank.payment.dto;

import java.math.BigDecimal;

public class PaymentResponse {
    private String paymentId;
    private String message;
    private BigDecimal newBalance;

    public PaymentResponse(String paymentId, String message, BigDecimal newBalance) {
        this.paymentId = paymentId;
        this.message = message;
        this.newBalance = newBalance;
    }

    public String getPaymentId() { return paymentId; }
    public String getMessage() { return message; }
    public BigDecimal getNewBalance() { return newBalance; }
}