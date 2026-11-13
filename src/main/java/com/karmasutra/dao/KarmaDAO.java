package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.KarmaTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KarmaDAO {

    public KarmaTransaction createTransaction(KarmaTransaction tx) {
        String insertSql = "INSERT INTO karma_transactions (user_id, amount, transaction_type, reason) VALUES (?, ?, ?, ?)";
        String updateKarmaSql = "UPDATE users SET karma_points = karma_points + ? WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psUpdate = conn.prepareStatement(updateKarmaSql)) {

                psInsert.setInt(1, tx.getUserId());
                psInsert.setInt(2, tx.getAmount());
                psInsert.setString(3, tx.getTransactionType());
                psInsert.setString(4, tx.getReason());
                psInsert.executeUpdate();

                try (ResultSet generatedKeys = psInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        tx.setId(generatedKeys.getInt(1));
                    }
                }

                psUpdate.setInt(1, tx.getAmount());
                psUpdate.setInt(2, tx.getUserId());
                psUpdate.executeUpdate();

                conn.commit();
                return tx;
            } catch (SQLException e) {
                conn.rollback();
                throw new DatabasePersistenceException("Transaction failed: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Database error recording karma transaction: " + e.getMessage(), e);
        }
    }

    public List<KarmaTransaction> listByUserId(int userId) {
        List<KarmaTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM karma_transactions WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KarmaTransaction tx = new KarmaTransaction();
                    tx.setId(rs.getInt("id"));
                    tx.setUserId(rs.getInt("user_id"));
                    tx.setAmount(rs.getInt("amount"));
                    tx.setTransactionType(rs.getString("transaction_type"));
                    tx.setReason(rs.getString("reason"));
                    tx.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(tx);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing karma transactions: " + e.getMessage(), e);
        }
        return list;
    }
}
