package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.SimulatedPayment;
import com.karmasutra.model.Subscription;
import com.karmasutra.model.enums.SubscriptionTier;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionDAO {

    /**
     * Executes atomic multi-table JDBC transaction for subscription activation,
     * payment ledger recording, and user tier upgrade.
     */
    public boolean processSubscriptionUpgrade(int userId, SubscriptionTier tier, String paymentMethod) {
        String insertSubSql = "INSERT INTO subscriptions (user_id, plan_tier, price, start_date, expiry_date, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        String insertPaySql = "INSERT INTO simulated_payments (user_id, subscription_id, transaction_reference, amount, currency, payment_status, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";
        String updateUserTierSql = "UPDATE users SET tier = ? WHERE id = ?";

        LocalDate now = LocalDate.now();
        LocalDate expiry = now.plusMonths(1);
        String txRef = "COSMIC-TXN-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 9000 + 1000);

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psSub = conn.prepareStatement(insertSubSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psPay = conn.prepareStatement(insertPaySql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psUser = conn.prepareStatement(updateUserTierSql)) {

                // 1. Deactivate old active subscriptions for user
                try (PreparedStatement psDeact = conn.prepareStatement("UPDATE subscriptions SET is_active = FALSE WHERE user_id = ?")) {
                    psDeact.setInt(1, userId);
                    psDeact.executeUpdate();
                }

                // 2. Insert new Subscription
                psSub.setInt(1, userId);
                psSub.setString(2, tier.name());
                psSub.setDouble(3, tier.getMonthlyPrice());
                psSub.setDate(4, Date.valueOf(now));
                psSub.setDate(5, Date.valueOf(expiry));
                psSub.setBoolean(6, true);
                psSub.executeUpdate();

                int subId = 0;
                try (ResultSet rs = psSub.getGeneratedKeys()) {
                    if (rs.next()) {
                        subId = rs.getInt(1);
                    }
                }

                // 3. Insert Simulated Payment record
                psPay.setInt(1, userId);
                psPay.setInt(2, subId);
                psPay.setString(3, txRef);
                psPay.setDouble(4, tier.getMonthlyPrice());
                psPay.setString(5, "INR");
                psPay.setString(6, "COMPLETED");
                psPay.setString(7, paymentMethod != null ? paymentMethod : "COSMIC_UPI");
                psPay.executeUpdate();

                // 4. Update User tier
                psUser.setString(1, tier.name());
                psUser.setInt(2, userId);
                psUser.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabasePersistenceException("Subscription transaction aborted: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Database connection error during subscription upgrade: " + e.getMessage(), e);
        }
    }

    public Subscription findActiveByUserId(int userId) {
        String sql = "SELECT * FROM subscriptions WHERE user_id = ? AND is_active = TRUE AND expiry_date >= CURRENT_DATE() ORDER BY id DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Subscription s = new Subscription();
                    s.setId(rs.getInt("id"));
                    s.setUserId(rs.getInt("user_id"));
                    s.setPlanTier(SubscriptionTier.valueOf(rs.getString("plan_tier")));
                    s.setPrice(rs.getDouble("price"));
                    s.setStartDate(rs.getDate("start_date"));
                    s.setExpiryDate(rs.getDate("expiry_date"));
                    s.setActive(rs.getBoolean("is_active"));
                    s.setCreatedAt(rs.getTimestamp("created_at"));
                    return s;
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error querying active subscription: " + e.getMessage(), e);
        }
        return null;
    }

    public List<SimulatedPayment> listPaymentsByUserId(int userId) {
        List<SimulatedPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM simulated_payments WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SimulatedPayment p = new SimulatedPayment();
                    p.setId(rs.getInt("id"));
                    p.setUserId(rs.getInt("user_id"));
                    p.setSubscriptionId(rs.getInt("subscription_id"));
                    p.setTransactionReference(rs.getString("transaction_reference"));
                    p.setAmount(rs.getDouble("amount"));
                    p.setCurrency(rs.getString("currency"));
                    p.setPaymentStatus(rs.getString("payment_status"));
                    p.setPaymentMethod(rs.getString("payment_method"));
                    p.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing payments: " + e.getMessage(), e);
        }
        return list;
    }

    public List<SimulatedPayment> listAllPayments() {
        List<SimulatedPayment> list = new ArrayList<>();
        String sql = "SELECT * FROM simulated_payments ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                SimulatedPayment p = new SimulatedPayment();
                p.setId(rs.getInt("id"));
                p.setUserId(rs.getInt("user_id"));
                p.setSubscriptionId(rs.getInt("subscription_id"));
                p.setTransactionReference(rs.getString("transaction_reference"));
                p.setAmount(rs.getDouble("amount"));
                p.setCurrency(rs.getString("currency"));
                p.setPaymentStatus(rs.getString("payment_status"));
                p.setPaymentMethod(rs.getString("payment_method"));
                p.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(p);
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing all payments: " + e.getMessage(), e);
        }
        return list;
    }
}
