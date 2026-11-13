package com.karmasutra.dao;

import com.karmasutra.config.DatabaseConfig;
import com.karmasutra.exception.DatabasePersistenceException;
import com.karmasutra.model.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<Department> listAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY id ASC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error listing departments: " + e.getMessage(), e);
        }
        return list;
    }

    public Department findById(int id) {
        String sql = "SELECT * FROM departments WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error finding department by id: " + e.getMessage(), e);
        }
        return null;
    }

    public Department findByCode(String code) {
        String sql = "SELECT * FROM departments WHERE code = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            throw new DatabasePersistenceException("Error finding department by code: " + e.getMessage(), e);
        }
        return null;
    }

    private Department mapRow(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setId(rs.getInt("id"));
        dept.setCode(rs.getString("code"));
        dept.setName(rs.getString("name"));
        dept.setDivineHead(rs.getString("divine_head"));
        dept.setDescription(rs.getString("description"));
        dept.setEscalationProtocol(rs.getString("escalation_protocol"));
        dept.setCreatedAt(rs.getTimestamp("created_at"));
        return dept;
    }
}
