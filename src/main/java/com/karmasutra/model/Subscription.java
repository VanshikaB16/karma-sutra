package com.karmasutra.model;

import com.karmasutra.model.enums.SubscriptionTier;
import java.sql.Date;
import java.sql.Timestamp;

public class Subscription {
    private int id;
    private int userId;
    private SubscriptionTier planTier;
    private double price;
    private Date startDate;
    private Date expiryDate;
    private boolean active;
    private Timestamp createdAt;

    public Subscription() {
        this.active = true;
    }

    public Subscription(int id, int userId, SubscriptionTier planTier, double price,
                        Date startDate, Date expiryDate, boolean active, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.planTier = planTier;
        this.price = price;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.active = active;
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

    public SubscriptionTier getPlanTier() {
        return planTier;
    }

    public void setPlanTier(SubscriptionTier planTier) {
        this.planTier = planTier;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
