package com.karmasutra.model;

import com.karmasutra.model.enums.SubscriptionTier;
import com.karmasutra.model.enums.UserRole;
import java.sql.Timestamp;

/**
 * Represents a registered cosmic devotee or administrative deity.
 * Strict encapsulation with defensive validation.
 */
public class User {

    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String salt;
    private String fullName;
    private UserRole role;
    private SubscriptionTier tier;
    private int karmaPoints;
    private Timestamp createdAt;

    public User() {
        this.role = UserRole.DEVOTEE;
        this.tier = SubscriptionTier.FREE_DEVOTEE;
        this.karmaPoints = 108;
    }

    public User(int id, String username, String email, String passwordHash, String salt,
                String fullName, UserRole role, SubscriptionTier tier, int karmaPoints, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.fullName = fullName;
        this.role = role != null ? role : UserRole.DEVOTEE;
        this.tier = tier != null ? tier : SubscriptionTier.FREE_DEVOTEE;
        this.karmaPoints = karmaPoints;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Valid email required");
        }
        this.email = email.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName != null ? fullName.trim() : "Unknown Devotee";
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public SubscriptionTier getTier() {
        return tier;
    }

    public void setTier(SubscriptionTier tier) {
        this.tier = tier;
    }

    public int getKarmaPoints() {
        return karmaPoints;
    }

    public void setKarmaPoints(int karmaPoints) {
        this.karmaPoints = karmaPoints;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAdmin() {
        return role == UserRole.ARCH_DEITY_ADMIN;
    }

    public boolean isPro() {
        return tier == SubscriptionTier.KARMA_SUTRA_PRO || tier == SubscriptionTier.VIP_DEVOTEE;
    }
}
