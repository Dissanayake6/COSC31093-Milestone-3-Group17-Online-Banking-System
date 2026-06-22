package com.bank.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String paymentId;

    @Column(nullable = false)
    private String fromAccount;

    @Column(nullable = false)
    private String billType;

    @Column(nullable = false)
    private String billerName;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String username;

    private LocalDateTime paymentDate;
    private String description;

    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
        if (paymentId == null) {
            paymentId = "PAY" + System.currentTimeMillis();
        }
        if (status == null) {
            status = "COMPLETED";
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getPaymentId() { return paymentId; }
    public String getFromAccount() { return fromAccount; }
    public String getBillType() { return billType; }
    public String getBillerName() { return billerName; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getUsername() { return username; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
    public String getDescription() { return description; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public void setFromAccount(String fromAccount) { this.fromAccount = fromAccount; }
    public void setBillType(String billType) { this.billType = billType; }
    public void setBillerName(String billerName) { this.billerName = billerName; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
    public void setUsername(String username) { this.username = username; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
    public void setDescription(String description) { this.description = description; }
}