package com.karmasutra.model;

import java.sql.Timestamp;

public class KarmaTransaction {
    private int id;
    private int userId;
    private int amount;
    private String transactionType; // "EARNED", "DEDUCTED", "COSMIC_DIVIDEND"
    private String reason;
    private Timestamp createdAt;

    public KarmaTransaction() {
    }

    public KarmaTransaction(int id, int userId, int amount, String transactionType, String reason, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.reason = reason;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
