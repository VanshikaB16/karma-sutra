package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.ComplaintStatusHistory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StatusHistoryDAO {

    public ComplaintStatusHistory create(ComplaintStatusHistory history) {
        String sql = "INSERT INTO complaint_status_history (complaint_id, old_status, new_status, changed_by_user_id, remarks) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, history.getComplaintId());
            ps.setString(2, history.getOldStatus());
            ps.setString(3, history.getNewStatus());
            if (history.getChangedByUserId() != null) {
                ps.setInt(4, history.getChangedByUserId());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, history.getRemarks());

            ps.executeUpdate();
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    history.setId(generatedKeys.getInt(1));
                }
            }
            return history;
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error recording status history: " + e.getMessage(), e);
        }
    }

    public List<ComplaintStatusHistory> listByComplaintId(int complaintId) {
        List<ComplaintStatusHistory> list = new ArrayList<>();
        String sql = "SELECT h.*, u.username AS changed_by_username " +
                     "FROM complaint_status_history h " +
                     "LEFT JOIN users u ON h.changed_by_user_id = u.id " +
                     "WHERE h.complaint_id = ? ORDER BY h.changed_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComplaintStatusHistory h = new ComplaintStatusHistory();
                    h.setId(rs.getInt("id"));
                    h.setComplaintId(rs.getInt("complaint_id"));
                    h.setOldStatus(rs.getString("old_status"));
                    h.setNewStatus(rs.getString("new_status"));
                    int changedBy = rs.getInt("changed_by_user_id");
                    if (!rs.wasNull()) {
                        h.setChangedByUserId(changedBy);
                    }
                    h.setChangedByUsername(rs.getString("changed_by_username"));
                    h.setRemarks(rs.getString("remarks"));
                    h.setChangedAt(rs.getTimestamp("changed_at"));
                    list.add(h);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing status history: " + e.getMessage(), e);
        }
        return list;
    }
}
