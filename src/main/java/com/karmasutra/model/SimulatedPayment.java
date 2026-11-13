package com.karmasutra.model;

import java.sql.Timestamp;

public class SimulatedPayment {
    private int id;
    private int userId;
    private Integer subscriptionId;
    private String transactionReference;
    private double amount;
    private String currency;
    private String paymentStatus; // "COMPLETED", "FAILED", "PENDING"
    private String paymentMethod; // "COSMIC_UPI", "HEAVENLY_NETBANKING", "KARMIC_CREDIT"
    private Timestamp createdAt;

    public SimulatedPayment() {
        this.currency = "INR";
        this.paymentStatus = "COMPLETED";
        this.paymentMethod = "COSMIC_UPI";
    }

    public SimulatedPayment(int id, int userId, Integer subscriptionId, String transactionReference,
                            double amount, String currency, String paymentStatus, String paymentMethod, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.subscriptionId = subscriptionId;
        this.transactionReference = transactionReference;
        this.amount = amount;
        this.currency = currency;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
