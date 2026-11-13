package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.WishMilestone;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MilestoneDAO {

    public WishMilestone create(WishMilestone milestone) {
        String sql = "INSERT INTO wish_milestones (complaint_id, title, target_date, status, cosmic_note) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, milestone.getComplaintId());
            ps.setString(2, milestone.getTitle());
            if (milestone.getTargetDate() != null) {
                ps.setDate(3, milestone.getTargetDate());
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, milestone.getStatus());
            ps.setString(5, milestone.getCosmicNote());
            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    milestone.setId(generatedKeys.getInt(1));
                }
            }
            return milestone;
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error creating wish milestone: " + e.getMessage(), e);
        }
    }

    public List<WishMilestone> listByComplaintId(int complaintId) {
        List<WishMilestone> list = new ArrayList<>();
        String sql = "SELECT * FROM wish_milestones WHERE complaint_id = ? ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WishMilestone m = new WishMilestone();
                    m.setId(rs.getInt("id"));
                    m.setComplaintId(rs.getInt("complaint_id"));
                    m.setTitle(rs.getString("title"));
                    m.setTargetDate(rs.getDate("target_date"));
                    m.setStatus(rs.getString("status"));
                    m.setCosmicNote(rs.getString("cosmic_note"));
                    m.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing milestones: " + e.getMessage(), e);
        }
        return list;
    }
}
