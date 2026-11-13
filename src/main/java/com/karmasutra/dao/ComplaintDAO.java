package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.complaint.Complaint;
import com.karmasutra.model.complaint.ComplaintFactory;
import com.karmasutra.model.enums.ComplaintCategory;
import com.karmasutra.model.enums.ComplaintType;
import com.karmasutra.model.enums.Severity;
import com.karmasutra.model.enums.TicketStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplaintDAO {

    public Complaint create(Complaint complaint) {
        String sql = "INSERT INTO complaints (ticket_number, user_id, subject, description, category, " +
                     "complaint_type, severity, department_id, status, desired_resolution, is_long_term_wish, " +
                     "is_escalated, karma_points_awarded) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, complaint.getTicketNumber());
            ps.setInt(2, complaint.getUserId());
            ps.setString(3, complaint.getSubject());
            ps.setString(4, complaint.getDescription());
            ps.setString(5, complaint.getCategory().name());
            ps.setString(6, complaint.getType().name());
            ps.setString(7, complaint.getSeverity().name());
            ps.setInt(8, complaint.getDepartmentId());
            ps.setString(9, complaint.getStatus().getLabel());
            ps.setString(10, complaint.getDesiredResolution());
            ps.setBoolean(11, complaint.isLongTermWish());
            ps.setBoolean(12, complaint.isEscalated());
            ps.setInt(13, complaint.getKarmaPointsAwarded());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabasePersistenceException("Failed to insert complaint ticket.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    complaint.setId(generatedKeys.getInt(1));
                }
            }
            return complaint;
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Database error saving complaint: " + e.getMessage(), e);
        }
    }

    public Complaint findById(int id) {
        String sql = "SELECT c.*, d.name AS dept_name, u.full_name AS user_fullname " +
                     "FROM complaints c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "WHERE c.id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error finding complaint by id: " + e.getMessage(), e);
        }
        return null;
    }

    public Complaint findByTicketNumber(String ticketNumber) {
        String sql = "SELECT c.*, d.name AS dept_name, u.full_name AS user_fullname " +
                     "FROM complaints c " +
                     "LEFT JOIN departments d ON c.department_id = d.id " +
                     "LEFT JOIN users u ON c.user_id = u.id " +
                     "WHERE c.ticket_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ticketNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error finding complaint by ticket number: " + e.getMessage(), e);
        }
        return null;
    }

    public List<Complaint> listByUserId(int userId, String categoryFilter, String statusFilter, String searchQuery) {
        return queryComplaints("c.user_id = ?", String.valueOf(userId), categoryFilter, statusFilter, searchQuery);
    }

    public List<Complaint> listAll(String categoryFilter, String statusFilter, String searchQuery) {
        return queryComplaints("1=1", null, categoryFilter, statusFilter, searchQuery);
    }

    private List<Complaint> queryComplaints(String baseCondition, String baseParam, String categoryFilter, String statusFilter, String searchQuery) {
        List<Complaint> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT c.*, d.name AS dept_name, u.full_name AS user_fullname " +
                "FROM complaints c " +
                "LEFT JOIN departments d ON c.department_id = d.id " +
                "LEFT JOIN users u ON c.user_id = u.id " +
                "WHERE " + baseCondition + " "
        );

        List<Object> params = new ArrayList<>();
        if (baseParam != null) {
            params.add(Integer.parseInt(baseParam));
        }

        if (categoryFilter != null && !categoryFilter.isBlank() && !categoryFilter.equalsIgnoreCase("All Categories")) {
            sql.append("AND c.category = ? ");
            params.add(ComplaintCategory.fromLabel(categoryFilter).name());
        }

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("All Statuses")) {
            sql.append("AND c.status = ? ");
            params.add(statusFilter);
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            sql.append("AND (c.ticket_number LIKE ? OR c.subject LIKE ? OR c.description LIKE ?) ");
            String wild = "%" + searchQuery.trim() + "%";
            params.add(wild);
            params.add(wild);
            params.add(wild);
        }

        sql.append("ORDER BY c.created_at DESC");

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error querying complaints: " + e.getMessage(), e);
        }
        return list;
    }

    public void updateStatus(int complaintId, TicketStatus newStatus) {
        String sql = "UPDATE complaints SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus.getLabel());
            ps.setInt(2, complaintId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error updating complaint status: " + e.getMessage(), e);
        }
    }

    public void updateDepartment(int complaintId, int newDepartmentId) {
        String sql = "UPDATE complaints SET department_id = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newDepartmentId);
            ps.setInt(2, complaintId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error updating complaint department: " + e.getMessage(), e);
        }
    }

    public void markEscalated(int complaintId, boolean escalated) {
        String sql = "UPDATE complaints SET is_escalated = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, escalated);
            ps.setInt(2, complaintId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error escalating complaint: " + e.getMessage(), e);
        }
    }

    public Map<String, Integer> getCountsByStatus(int userId) {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) AS cnt FROM complaints WHERE user_id = ? GROUP BY status";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    counts.put(rs.getString("status"), rs.getInt("cnt"));
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error getting status counts: " + e.getMessage(), e);
        }
        return counts;
    }

    public Map<String, Integer> getAllCountsByStatus() {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT status, COUNT(*) AS cnt FROM complaints GROUP BY status";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                counts.put(rs.getString("status"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error getting all status counts: " + e.getMessage(), e);
        }
        return counts;
    }

    private Complaint mapRow(ResultSet rs) throws SQLException {
        ComplaintType type = ComplaintType.valueOf(rs.getString("complaint_type"));
        Complaint complaint = ComplaintFactory.createComplaint(type);

        complaint.setId(rs.getInt("id"));
        complaint.setTicketNumber(rs.getString("ticket_number"));
        complaint.setUserId(rs.getInt("user_id"));
        complaint.setSubject(rs.getString("subject"));
        complaint.setDescription(rs.getString("description"));
        complaint.setCategory(ComplaintCategory.valueOf(rs.getString("category")));
        complaint.setType(type);
        complaint.setSeverity(Severity.valueOf(rs.getString("severity")));
        complaint.setDepartmentId(rs.getInt("department_id"));
        complaint.setStatus(TicketStatus.fromLabel(rs.getString("status")));
        complaint.setDesiredResolution(rs.getString("desired_resolution"));
        complaint.setLongTermWish(rs.getBoolean("is_long_term_wish"));
        complaint.setEscalated(rs.getBoolean("is_escalated"));
        complaint.setKarmaPointsAwarded(rs.getInt("karma_points_awarded"));
        complaint.setCreatedAt(rs.getTimestamp("created_at"));
        complaint.setUpdatedAt(rs.getTimestamp("updated_at"));

        try {
            complaint.setDepartmentName(rs.getString("dept_name"));
        } catch (SQLException ignored) {
        }
        try {
            complaint.setSubmitterName(rs.getString("user_fullname"));
        } catch (SQLException ignored) {
        }

        return complaint;
    }
}
