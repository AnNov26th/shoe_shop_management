package com.pbl_3project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import com.pbl_3project.util.DatabaseConnection;

public class EmployeeDAO {
    public DefaultTableModel searchEmployees(String keyword) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String[] columnNames = { "ID", "Họ Tên", "Email", "SĐT", "Chức vụ", "Trạng thái" };
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "SELECT id, full_name, email, phone, role_id, status FROM [User] " +
                    "WHERE role_id IN (2, 3) ";
            if (keyword != null && !keyword.trim().isEmpty()) {
                sql += "AND (full_name LIKE ? OR email LIKE ? OR phone LIKE ?) ";
            }
            sql += "ORDER BY id DESC";
            pstmt = conn.prepareStatement(sql);
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.trim() + "%";
                pstmt.setString(1, searchPattern);
                pstmt.setString(2, searchPattern);
                pstmt.setString(3, searchPattern);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String roleName = rs.getInt("role_id") == 2 ? "Quản lý (Manager)" : "Nhân viên (Staff)";
                String name = rs.getString("full_name") != null ? rs.getString("full_name") : "Chưa cập nhật";
                model.addRow(new Object[] {
                        rs.getInt("id"), name, rs.getString("email"),
                        rs.getString("phone"), roleName, rs.getString("status")
                });
            }
        } finally {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
        return model;
    }

    public boolean addEmployee(String fullName, String email, String phone, int roleId, String password)
            throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.getConnection();
            String sql = "INSERT INTO [User] (full_name, email, phone, role_id, password_hash, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, 'Active', GETDATE())";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setInt(4, roleId);
            pstmt.setString(5, password);
            return pstmt.executeUpdate() > 0;
        } finally {
            if (pstmt != null)
                pstmt.close();
            DatabaseConnection.closeConnection(conn);
        }
    }
}
