package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.ComplaintResponse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResponseDAO {

    public ComplaintResponse create(ComplaintResponse response) {
        String sql = "INSERT INTO complaint_responses (complaint_id, responder_type, divine_persona, " +
                     "response_text, emotional_tone, iteration_number) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, response.getComplaintId());
            ps.setString(2, response.getResponderType());
            ps.setString(3, response.getDivinePersona());
            ps.setString(4, response.getResponseText());
            ps.setString(5, response.getEmotionalTone());
            ps.setInt(6, response.getIterationNumber());

            int affected = ps.executeUpdate();
            if (affected == 0) {
                throw new DatabasePersistenceException("Failed to save divine response.");
            }
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    response.setId(generatedKeys.getInt(1));
                }
            }
            return response;
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Database error saving response: " + e.getMessage(), e);
        }
    }

    public List<ComplaintResponse> listByComplaintId(int complaintId) {
        List<ComplaintResponse> list = new ArrayList<>();
        String sql = "SELECT * FROM complaint_responses WHERE complaint_id = ? ORDER BY iteration_number ASC, created_at ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing responses for complaint: " + e.getMessage(), e);
        }
        return list;
    }

    public ComplaintResponse getLatestByComplaintId(int complaintId) {
        String sql = "SELECT * FROM complaint_responses WHERE complaint_id = ? ORDER BY iteration_number DESC, id DESC LIMIT 1";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, complaintId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error getting latest response: " + e.getMessage(), e);
        }
        return null;
    }

    private ComplaintResponse mapRow(ResultSet rs) throws SQLException {
        ComplaintResponse r = new ComplaintResponse();
        r.setId(rs.getInt("id"));
        r.setComplaintId(rs.getInt("complaint_id"));
        r.setResponderType(rs.getString("responder_type"));
        r.setDivinePersona(rs.getString("divine_persona"));
        r.setResponseText(rs.getString("response_text"));
        r.setEmotionalTone(rs.getString("emotional_tone"));
        r.setIterationNumber(rs.getInt("iteration_number"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }
}
